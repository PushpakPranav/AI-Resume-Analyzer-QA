from fastapi import APIRouter, Depends, Request
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates
from sqlalchemy.orm import Session

from app.core.database   import get_db
from app.core.security   import generate_csrf_token, set_csrf_cookie, set_no_cache_headers
from app.models.resume   import Resume
from app.models.analysis import Analysis
from app.routes.auth     import get_current_user
from app.models.user     import User

router    = APIRouter(tags=["Dashboard"])
templates = Jinja2Templates(directory="app/templates")


@router.get("/dashboard", response_class=HTMLResponse)
async def dashboard(
    request: Request,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    if not current_user:
        return RedirectResponse(url="/auth/login", status_code=302)


    deleted_param = request.query_params.get("deleted")
    delete_message = None
    if deleted_param == "1":
        delete_message = {"type": "success", "text": "Resume deleted successfully."}
    elif deleted_param == "0":
        delete_message = {"type": "danger", "text": "Could not delete resume. Please try again."}

    resumes   = db.query(Resume).filter(Resume.user_id == current_user.id)\
                  .order_by(Resume.uploaded_at.desc()).all()
    total     = len(resumes)
    avg_score = round(sum(r.ats_score for r in resumes) / total, 1) if total else 0
    best      = max(resumes, key=lambda r: r.ats_score, default=None)
    domains   = list({r.detected_domain for r in resumes if r.detected_domain})

    latest_10 = resumes[:10]
    history_labels = [r.filename[:15] for r in reversed(latest_10)]
    history_scores = [r.ats_score for r in reversed(latest_10)]

    csrf_token = generate_csrf_token()
    resp = templates.TemplateResponse(
        request=request, name="dashboard.html", context={
            "current_user":   current_user,
            "resumes":        resumes,
            "total":          total,
            "avg_score":      avg_score,
            "best":           best,
            "domains":        domains,
            "history_labels": history_labels,
            "history_scores": history_scores,
            "delete_message": delete_message,
            "csrf_token":     csrf_token,
        }
    )
    set_csrf_cookie(resp, csrf_token)
    set_no_cache_headers(resp)
    return resp