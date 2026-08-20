import re
from fastapi import APIRouter, Depends, Request, Form, Response
from fastapi.responses import HTMLResponse, RedirectResponse, JSONResponse
from fastapi.templating import Jinja2Templates
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.security import (
    hash_password, verify_password, create_access_token, decode_token,
    create_reset_token, decode_reset_token,
    generate_csrf_token, set_csrf_cookie, csrf_token_valid,
    register_failed_login, clear_login_attempts, login_lockout_seconds_remaining,
    set_no_cache_headers,
)
from app.models.user import User

router    = APIRouter(prefix="/auth", tags=["Auth"])
templates = Jinja2Templates(directory="app/templates")


def validate_password(password: str):
    """Returns an error message string if password is weak, else None."""
    if password != password.strip():
        return "Password must not have leading or trailing spaces"
    if len(password) < 8:
        return "Password must contain at least 8 characters"
    if not re.search(r"[A-Z]", password):
        return "Password must contain at least one uppercase letter"
    if not re.search(r"[a-z]", password):
        return "Password must contain at least one lowercase letter"
    if not re.search(r"[0-9]", password):
        return "Password must contain at least one number"
    if not re.search(r"[^A-Za-z0-9\s]", password):
        return "Password must contain at least one special character"
    return None


def get_current_user(request: Request, db: Session = Depends(get_db)):
    """FastAPI dependency used on nearly every route to identify the logged-in
    user from the access_token cookie. Returns None (never raises) for a
    logged-out visitor, missing/expired/tampered token, or a token whose
    user was since deleted — callers treat None as "not authenticated" and
    decide themselves whether to redirect, 401, or just render a logged-out
    view (see home() in main.py for the logged-out-is-fine case)."""
    token = request.cookies.get("access_token")
    if not token:
        return None
    payload = decode_token(token)
    if not payload:
        return None
    return db.query(User).filter(User.id == payload.get("sub")).first()


@router.get("/login", response_class=HTMLResponse)
async def login_page(request: Request):
    token = generate_csrf_token()
    resp = templates.TemplateResponse(
        request=request, name="auth/login.html", context={"csrf_token": token}
    )
    set_csrf_cookie(resp, token)
    set_no_cache_headers(resp)
    return resp


@router.post("/login")
async def login(
    request: Request,
    email: str = Form(...), password: str = Form(...),
    csrf_token: str = Form(...),
    db: Session = Depends(get_db)
):

    if not csrf_token_valid(request, csrf_token):
        return JSONResponse({"error": "Your session expired. Please refresh and try again."}, status_code=403)

    identifier = email.strip().lower()

    # Bug fix: throttle brute-force login attempts (previously unlimited)
    remaining = login_lockout_seconds_remaining(identifier)
    if remaining:
        resp = JSONResponse({"error": "Too many attempts. Please try again later."}, status_code=429)
        resp.headers["Retry-After"] = str(remaining)
        return resp

    user = db.query(User).filter(User.email == email).first()
    if not user or not verify_password(password, user.hashed_password):
        register_failed_login(identifier)
        return JSONResponse({"error": "Invalid email or password"}, status_code=200)

    clear_login_attempts(identifier)
    token = create_access_token({"sub": str(user.id)})
    resp = JSONResponse({"success": True, "redirect": "/dashboard"})
    resp.set_cookie("access_token", token, httponly=True, max_age=86400, samesite="lax")
    return resp


@router.get("/register", response_class=HTMLResponse)
async def register_page(request: Request):
    resp = templates.TemplateResponse(
        request=request, name="auth/register.html", context={}
    )
    set_no_cache_headers(resp)
    return resp


@router.post("/register")
async def register(
    request: Request,
    name: str = Form(...), email: str = Form(...),
    password: str = Form(...), db: Session = Depends(get_db)
):
    if db.query(User).filter(User.email == email).first():
        return JSONResponse({"error": "Email already registered"}, status_code=200)

    pwd_error = validate_password(password)
    if pwd_error:
        return JSONResponse({"error": pwd_error}, status_code=200)

    user = User(name=name, email=email, hashed_password=hash_password(password), avatar=name[0].upper() if name else "U")
    db.add(user)
    db.commit()
    db.refresh(user)
    token = create_access_token({"sub": str(user.id)})
    resp = JSONResponse({"success": True, "redirect": "/dashboard"})
    resp.set_cookie("access_token", token, httponly=True, max_age=86400, samesite="lax")
    return resp


@router.get("/logout")
async def logout():
    resp = RedirectResponse(url="/", status_code=302)
    resp.delete_cookie("access_token")
    return resp


# ---------------------------------------------------------------------------
# Forgot Password / Reset Password
# ---------------------------------------------------------------------------

@router.get("/forgot-password", response_class=HTMLResponse)
async def forgot_password_page(request: Request):
    resp = templates.TemplateResponse(
        request=request, name="auth/forgot_password.html", context={}
    )
    set_no_cache_headers(resp)
    return resp


@router.post("/forgot-password", response_class=HTMLResponse)
async def forgot_password(
    request: Request, email: str = Form(...), db: Session = Depends(get_db)
):
    email = email.strip()
    user = db.query(User).filter(User.email == email).first()

    reset_link = None
    if user:
        token = create_reset_token(user.id)
        reset_link = str(request.url_for("reset_password_page", token=token))
        print(f"[Forgot Password] Reset link for {user.email}: {reset_link}")
        resp = templates.TemplateResponse(
        request=request, name="auth/forgot_password.html",
        context={
            "message": "If an account exists for that email, a password reset link has been generated.",
            "reset_link": reset_link,  # shown only in this dev build; see NOTE above
        }
    )
    set_no_cache_headers(resp)
    return resp


@router.get("/reset-password/{token}", response_class=HTMLResponse, name="reset_password_page")
async def reset_password_page(request: Request, token: str, db: Session = Depends(get_db)):
    payload = decode_reset_token(token)
    if not payload or not db.query(User).filter(User.id == payload.get("sub")).first():
        resp = templates.TemplateResponse(
            request=request, name="auth/reset_password.html",
            context={"invalid": True}
        )
        return set_no_cache_headers(resp)
    resp = templates.TemplateResponse(
        request=request, name="auth/reset_password.html",
        context={"token": token}
    )
    set_no_cache_headers(resp)
    return resp


@router.post("/reset-password/{token}", response_class=HTMLResponse)
async def reset_password(
    request: Request, token: str,
    password: str = Form(...), confirm_password: str = Form(...),
    db: Session = Depends(get_db)
):
    payload = decode_reset_token(token)
    user = db.query(User).filter(User.id == payload.get("sub")).first() if payload else None
    if not user:
        resp = templates.TemplateResponse(
            request=request, name="auth/reset_password.html",
            context={"invalid": True}
        )
        return set_no_cache_headers(resp)

    if password != confirm_password:
        resp = templates.TemplateResponse(
            request=request, name="auth/reset_password.html",
            context={"token": token, "error": "Passwords do not match"}
        )
        return set_no_cache_headers(resp)

    pwd_error = validate_password(password)
    if pwd_error:
        resp = templates.TemplateResponse(
            request=request, name="auth/reset_password.html",
            context={"token": token, "error": pwd_error}
        )
        return set_no_cache_headers(resp)

    user.hashed_password = hash_password(password)
    db.commit()
    new_token = generate_csrf_token()
    resp = templates.TemplateResponse(
        request=request, name="auth/login.html",
        context={
            "message": "Password reset successful. Please log in with your new password.",
            "csrf_token": new_token,
        }
    )
    set_csrf_cookie(resp, new_token)
    set_no_cache_headers(resp)
    return resp