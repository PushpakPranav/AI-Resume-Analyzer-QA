from fastapi import APIRouter, UploadFile, File, Depends, Request, Form
from fastapi.responses import JSONResponse
from sqlalchemy.orm import Session
import os

from app.core.database import get_db
from app.routes.auth import get_current_user
from app.core.security import csrf_token_valid
from app.models.user import User



router = APIRouter(prefix="/profile", tags=["Profile"])

AVATAR_DIR = "app/static/avatars"
os.makedirs(AVATAR_DIR, exist_ok=True)
ALLOWED = {".jpg", ".jpeg", ".png", ".webp"}
MAX_AVATAR_SIZE = 2 * 1024 * 1024  # 2 MB

@router.post("/upload-avatar")
async def upload_avatar(
    request: Request,
    file: UploadFile = File(...),
    csrf_token: str = Form(...),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    if not current_user:
        return JSONResponse({"error": "Not logged in"}, status_code=401)

    if not csrf_token_valid(request, csrf_token):
        return JSONResponse({"error": "Invalid or missing CSRF token"}, status_code=403)

    ext = os.path.splitext(file.filename)[1].lower()
    if ext not in ALLOWED:
        return JSONResponse({"error": "Only JPG, PNG, WEBP allowed"}, status_code=400)

    content = await file.read()
    if len(content) > MAX_AVATAR_SIZE:
        return JSONResponse({"error": "Avatar must be under 2 MB"}, status_code=400)


    filename = f"user_{current_user.id}{ext}"
    filepath = os.path.join(AVATAR_DIR, filename)
    with open(filepath, "wb") as f:
        f.write(content)

  
    url = f"/static/avatars/{filename}"
    current_user.avatar = url
    db.commit()

    return JSONResponse({"avatar_url": url})