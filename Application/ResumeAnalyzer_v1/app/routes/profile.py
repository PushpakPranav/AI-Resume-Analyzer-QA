from fastapi import APIRouter, UploadFile, File, Depends, Request
from fastapi.responses import JSONResponse
from sqlalchemy.orm import Session
import os, shutil

from app.core.database import get_db
from app.routes.auth import get_current_user
from app.models.user import User

router = APIRouter(prefix="/profile", tags=["Profile"])

AVATAR_DIR = "app/static/avatars"
os.makedirs(AVATAR_DIR, exist_ok=True)
ALLOWED = {".jpg", ".jpeg", ".png", ".webp"}

@router.post("/upload-avatar")
async def upload_avatar(
    request: Request,
    file: UploadFile = File(...),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    if not current_user:
        return JSONResponse({"error": "Not logged in"}, status_code=401)

    ext = os.path.splitext(file.filename)[1].lower()
    if ext not in ALLOWED:
        return JSONResponse({"error": "Only JPG, PNG, WEBP allowed"}, status_code=400)

    # Save file
    filename = f"user_{current_user.id}{ext}"
    filepath = os.path.join(AVATAR_DIR, filename)
    with open(filepath, "wb") as f:
        shutil.copyfileobj(file.file, f)

    # Update DB
    url = f"/static/avatars/{filename}"
    current_user.avatar = url
    db.commit()

    return JSONResponse({"avatar_url": url})