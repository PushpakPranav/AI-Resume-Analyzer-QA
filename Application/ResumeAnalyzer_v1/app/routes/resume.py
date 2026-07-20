from fastapi import APIRouter, UploadFile, File, Depends, Request, HTTPException, Form
from fastapi.templating import Jinja2Templates
from fastapi.responses import RedirectResponse
from sqlalchemy.orm import Session
import os

from app.services.parser         import extract_text
from app.services.groq_service import groq_ats_score
from app.core.database           import get_db
from app.core.security           import csrf_token_valid
from app.models.resume           import Resume
from app.routes.auth             import get_current_user
from app.models.user             import User

router     = APIRouter(prefix="/resume", tags=["Resume"])
templates  = Jinja2Templates(directory="app/templates")
UPLOAD_DIR = "uploads"
MAX_FILE_SIZE = 5 * 1024 * 1024  # 5 MB
os.makedirs(UPLOAD_DIR, exist_ok=True)


@router.post("/upload")
async def upload_resume(
    request: Request,
    file: UploadFile = File(...),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    ext = os.path.splitext(file.filename)[1].lower()
    if ext not in (".pdf", ".docx"):
        return templates.TemplateResponse(
            request=request, name="index.html", status_code=400, context={
                "current_user": current_user,
                "error": "Only PDF and DOCX files are allowed",
            }
        )

    content = await file.read()
    if len(content) == 0:
        return templates.TemplateResponse(
            request=request, name="index.html", status_code=400, context={
                "current_user": current_user,
                "error": "File is empty.",
            }
        )

    if len(content) > MAX_FILE_SIZE:
        return templates.TemplateResponse(
            request=request, name="index.html", status_code=400, context={
                "current_user": current_user,
                "error": "File size must not exceed 5 MB.",
            }
        )

    safe_name = file.filename.replace(" ", "_")
    file_path = os.path.join(UPLOAD_DIR, safe_name)

    with open(file_path, "wb") as f:
        f.write(content)

    extracted_text = extract_text(file_path)
    if not extracted_text.strip():
        return templates.TemplateResponse(
            request=request, name="index.html", status_code=422, context={
                "current_user": current_user,
                "error": "Could not extract text. Ensure it is not a scanned image.",
            }
        )

    ats_result = groq_ats_score(extracted_text)

    resume = Resume(
        filename        = safe_name,
        extracted_text  = extracted_text,
        ats_score       = ats_result["ats_score"],
        grade           = ats_result["grade"],
        detected_domain = ats_result["detected_domain"],
        user_id         = current_user.id if current_user else None,
    )
    db.add(resume)
    db.commit()
    db.refresh(resume)

    return templates.TemplateResponse(
        request=request, name="result.html", context={
            "current_user":    current_user,
            "resume_id":       resume.id,
            "filename":        safe_name,
            "ats_score":       ats_result["ats_score"],
            "grade":           ats_result["grade"],
            "grade_color":     ats_result["grade_color"],
            "detected_domain": ats_result["detected_domain"],
            "ats_matched":     ats_result["matched_skills"],
            "ats_missing":     ats_result["missing_skills"],
            "summary":         ats_result["summary"],
        }
    )


@router.post("/delete/{resume_id}")
def delete_resume(
    request: Request,
    resume_id: int,
    csrf_token: str = Form(...),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    # Bug fix: this endpoint was previously GET (state-changing action via a
    # simple link/image request) with no CSRF check and no ownership check —
    # any resume with user_id = NULL could be deleted by anyone. It is now
    # POST-only, requires a valid CSRF token, and requires the resume to
    # belong to the logged-in user.
    if not csrf_token_valid(request, csrf_token):
        raise HTTPException(status_code=403, detail="Invalid or missing CSRF token")

    if not current_user:
        return RedirectResponse(url="/auth/login", status_code=302)

    resume = db.query(Resume).filter(Resume.id == resume_id).first()
    if resume and resume.user_id == current_user.id:
        db.delete(resume)
        db.commit()
        return RedirectResponse(url="/dashboard?deleted=1", status_code=302)
    return RedirectResponse(url="/dashboard?deleted=0", status_code=302)