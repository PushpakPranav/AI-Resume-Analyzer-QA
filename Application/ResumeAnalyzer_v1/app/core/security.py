from passlib.context import CryptContext
from jose import JWTError, jwt
from datetime import datetime, timedelta
import os
import secrets
import time
import threading
from collections import defaultdict

SECRET_KEY = os.getenv("SECRET_KEY")
if not SECRET_KEY:
    raise RuntimeError("SECRET_KEY not found — set it in your .env file")
ALGORITHM  = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60 * 24  # 1 day
RESET_TOKEN_EXPIRE_MINUTES  = 15       # Forgot Password reset link validity

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def hash_password(password: str) -> str:
    # bcrypt 72-byte limit fix for Python 3.12
    password_bytes = password.encode("utf-8")[:72]
    password_truncated = password_bytes.decode("utf-8", errors="ignore")
    return pwd_context.hash(password_truncated)

def verify_password(plain: str, hashed: str) -> bool:
    return pwd_context.verify(plain, hashed)

def create_access_token(data: dict) -> str:
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

def decode_token(token: str):
    try:
        return jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
    except JWTError:
        return None


def create_reset_token(user_id: int) -> str:
    """Short-lived, single-purpose JWT used only for the Forgot Password flow.
    Kept separate from create_access_token() so a reset link can never be
    replayed as a login session (enforced via the 'purpose' claim)."""
    expire = datetime.utcnow() + timedelta(minutes=RESET_TOKEN_EXPIRE_MINUTES)
    to_encode = {"sub": str(user_id), "purpose": "password_reset", "exp": expire}
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)


def decode_reset_token(token: str):
    """Returns the payload only if the token is valid AND was issued for
    password_reset — rejects a normal access_token passed in by mistake."""
    payload = decode_token(token)
    if not payload or payload.get("purpose") != "password_reset":
        return None
    return payload


# ---------------------------------------------------------------------------
# CSRF protection (Double-Submit Cookie pattern)
# Bug fix: forged cross-origin POST requests to /auth/login and
# /resume/delete/{id} were previously accepted with no verification.
# A random token is set as a readable (non-HttpOnly) cookie when the form
# page is loaded; the form echoes it back as a hidden field on submit.
# An attacker on another origin cannot read the cookie's value (browsers
# don't allow cross-origin cookie reads), so they cannot supply a matching
# hidden field, and the request is rejected.
# ---------------------------------------------------------------------------

CSRF_COOKIE_NAME = "csrf_token"

def generate_csrf_token() -> str:
    return secrets.token_urlsafe(32)

def set_csrf_cookie(response, token: str):
    response.set_cookie(
        CSRF_COOKIE_NAME, token,
        httponly=False,   # must be readable by the form-rendering page itself
        samesite="strict",
        max_age=1800,     # 30 minutes
    )

def csrf_token_valid(request, submitted_token: str) -> bool:
    cookie_token = request.cookies.get(CSRF_COOKIE_NAME)
    return bool(cookie_token) and bool(submitted_token) and secrets.compare_digest(cookie_token, submitted_token)


# ---------------------------------------------------------------------------
# Login brute-force lockout
# Bug fix: previously there was no limit on repeated failed login attempts.
# In-memory, per-process counter keyed by email — sufficient for this
# single-worker deployment. Resets automatically after LOCKOUT_SECONDS.
# ---------------------------------------------------------------------------

MAX_LOGIN_ATTEMPTS = 5
LOCKOUT_SECONDS     = 15 * 60  # 15 minutes

_failed_attempts = defaultdict(list)  # email -> [timestamp, ...]
_attempts_lock = threading.Lock()

def _prune(identifier: str, now: float):
    _failed_attempts[identifier] = [t for t in _failed_attempts[identifier] if now - t < LOCKOUT_SECONDS]

def register_failed_login(identifier: str):
    identifier = identifier.strip().lower()
    with _attempts_lock:
        now = time.time()
        _prune(identifier, now)
        _failed_attempts[identifier].append(now)

def clear_login_attempts(identifier: str):
    identifier = identifier.strip().lower()
    with _attempts_lock:
        _failed_attempts.pop(identifier, None)

def login_lockout_seconds_remaining(identifier: str) -> int:
    """Returns 0 if not locked out, else seconds until the oldest counted
    failure ages out of the lockout window."""
    identifier = identifier.strip().lower()
    with _attempts_lock:
        now = time.time()
        _prune(identifier, now)
        attempts = _failed_attempts[identifier]
        if len(attempts) < MAX_LOGIN_ATTEMPTS:
            return 0
        oldest = min(attempts)
        remaining = int(LOCKOUT_SECONDS - (now - oldest))
        return max(remaining, 1)


# ---------------------------------------------------------------------------
# No-cache headers for sensitive pages
# Bug fix: login/register/forgot-password/reset-password/dashboard responses
# had no Cache-Control header, so browsers could cache them (including
# rendered error/lockout pages). Pressing Back would then show a stale
# cached page instead of hitting the server again. This does NOT bypass the
# login lockout itself (that state lives server-side, not in the browser),
# but sensitive pages should never be cacheable by the browser.
# ---------------------------------------------------------------------------

def set_no_cache_headers(response):
    response.headers["Cache-Control"] = "no-store, no-cache, must-revalidate, max-age=0"
    response.headers["Pragma"] = "no-cache"
    response.headers["Expires"] = "0"
    return response