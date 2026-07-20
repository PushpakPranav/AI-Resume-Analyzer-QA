from fastapi import FastAPI, Request
from fastapi.templating import Jinja2Templates
from fastapi.staticfiles import StaticFiles
from fastapi.responses import HTMLResponse
from fastapi import Depends

from app.core.database import Base, engine
from app.models.user import User
from app.models.resume import Resume
from app.models.analysis import Analysis
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.routes.auth import get_current_user
from app.models.user import User

from app.routes.auth      import router as auth_router
from app.routes.resume    import router as resume_router
from app.routes.analysis  import router as analysis_router
from app.routes.dashboard import router as dashboard_router
from app.routes.profile import router as profile_router

app = FastAPI(
    title="Resume Analyzer v1",
    description="Advanced AI Resume Analyzer — Built by Pushpak Pranav",
    version="1.0.0"
)

Base.metadata.create_all(bind=engine)

app.mount("/static", StaticFiles(directory="app/static"), name="static")

app.include_router(auth_router)
app.include_router(resume_router)
app.include_router(analysis_router)
app.include_router(dashboard_router)
app.include_router(profile_router)


templates = Jinja2Templates(directory="app/templates")

@app.get("/", response_class=HTMLResponse)
async def home(
    request: Request,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    return templates.TemplateResponse(
        request=request, name="index.html", context={
            "current_user": current_user
        }
    )