import os
import json
from fastapi import APIRouter, Depends, HTTPException, Form, Request
from fastapi.templating import Jinja2Templates
from fastapi.responses import FileResponse
from sqlalchemy.orm import Session

from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.units import inch

from app.core.database           import get_db
from app.models.analysis         import Analysis
from app.models.resume           import Resume
from app.services.groq_service import groq_jd_match, groq_rewrite_bullets, groq_skill_roadmap, _grade_color
from app.routes.auth             import get_current_user
from app.models.user             import User

router      = APIRouter(prefix="/analysis", tags=["Analysis"])
templates   = Jinja2Templates(directory="app/templates")
REPORTS_DIR = "reports"
os.makedirs(REPORTS_DIR, exist_ok=True)


@router.post("/match")
def match_resume(
    request: Request,
    resume_id: int = Form(...),
    job_description: str = Form(...),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    resume = db.query(Resume).filter(Resume.id == resume_id).first()
    if not resume:
        raise HTTPException(404, "Resume not found")

    job_description_clean = job_description.strip()
    if not job_description_clean:
        return templates.TemplateResponse(
            request=request, name="result.html", status_code=400, context={
                "current_user":    current_user,
                "resume_id":       resume.id,
                "filename":        resume.filename,
                "ats_score":       resume.ats_score,
                "grade":           resume.grade,
                "grade_color":     _grade_color(resume.grade),
                "detected_domain": resume.detected_domain,
                "ats_matched":     [],
                "ats_missing":     [],
                "summary":         "",
                "error":           "Job description cannot be empty or contain only spaces.",
            }
        )

    match_result = groq_jd_match(resume.extracted_text, job_description_clean)
    rewrites     = groq_rewrite_bullets(resume.extracted_text)
    roadmap      = groq_skill_roadmap(
        match_result["missing_skills"],
        resume.detected_domain or "General"
    )

    analysis = Analysis(
        resume_id        = resume.id,
        job_description  = job_description_clean,
        match_percentage = match_result["match_percentage"],
        matched_skills   = ",".join(match_result["matched_skills"]),
        missing_skills   = ",".join(match_result["missing_skills"]),
        feedback         = "|".join(match_result.get("feedback", [])),
        suggestions      = "|".join(match_result.get("suggestions", [])),
        roadmap          = json.dumps(roadmap),
    )
    db.add(analysis)
    db.commit()
    db.refresh(analysis)

    return templates.TemplateResponse(
        request=request, name="match_result.html", context={
            "current_user":     current_user,
            "resume_id":        resume.id,
            "analysis_id":      analysis.id,
            "filename":         resume.filename,
            "match_percentage": match_result["match_percentage"],
            "matched_skills":   match_result["matched_skills"],
            "missing_skills":   match_result["missing_skills"],
            "feedback":         match_result["feedback"],
            "suggestions":      match_result["suggestions"],
            "roadmap":          roadmap,
            "rewrites":         rewrites,
            "ats_score":        resume.ats_score,
            "grade":            resume.grade,
            "detected_domain":  resume.detected_domain,
        }
    )


@router.get("/history/{resume_id}")
def get_history(
    resume_id: int,
    request: Request,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    resume = db.query(Resume).filter(Resume.id == resume_id).first()
    if not resume:
        raise HTTPException(404, "Resume not found")

    analyses = (
        db.query(Analysis)
        .filter(Analysis.resume_id == resume_id)
        .order_by(Analysis.created_at.desc())
        .all()
    )
    return templates.TemplateResponse(
        request=request, name="history.html", context={
            "current_user": current_user,
            "resume":       resume,
            "analyses":     analyses,
        }
    )


@router.get("/report/{analysis_id}")
def download_report(analysis_id: int, db: Session = Depends(get_db)):
    analysis = db.query(Analysis).filter(Analysis.id == analysis_id).first()
    if not analysis:
        raise HTTPException(404, "Analysis not found")

    resume  = db.query(Resume).filter(Resume.id == analysis.resume_id).first()
    matched = analysis.matched_skills.split(",") if analysis.matched_skills else []
    missing = analysis.missing_skills.split(",") if analysis.missing_skills else []

    pdf_path = os.path.join(REPORTS_DIR, f"ATS_Report_{analysis_id}.pdf")
    C_INDIGO = colors.HexColor("#4f46e5")
    C_TEAL   = colors.HexColor("#0d9488")
    C_LIGHT  = colors.HexColor("#f0f4ff")

    styles = getSampleStyleSheet()
    doc    = SimpleDocTemplate(pdf_path, pagesize=A4,
               rightMargin=inch, leftMargin=inch,
               topMargin=inch, bottomMargin=inch)

    title_s   = ParagraphStyle("T",  parent=styles["Title"],   fontSize=18, textColor=C_INDIGO, spaceAfter=8)
    heading_s = ParagraphStyle("H",  parent=styles["Heading2"],fontSize=12, textColor=C_TEAL,   spaceAfter=4)
    body_s    = ParagraphStyle("B",  parent=styles["Normal"],  fontSize=10, leading=14, spaceAfter=3)
    bullet_s  = ParagraphStyle("BL", parent=styles["Normal"],  fontSize=10, leading=14, leftIndent=16, spaceAfter=3)

    content = []
    content.append(Paragraph("ATS Resume Analysis Report", title_s))
    content.append(Paragraph("Built by Pratyush Kashyap | Powered by Groq AI (Llama 3.3 70B)", body_s))
    content.append(Spacer(1, 10))

    info_data = [
        ["Resume",         resume.filename],
        ["Domain",         resume.detected_domain or "N/A"],
        ["ATS Score",      f"{resume.ats_score}%  ({resume.grade})"],
        ["JD Match",       f"{analysis.match_percentage}%"],
        ["Matched Skills", str(len(matched))],
        ["Missing Skills", str(len(missing))],
    ]
    t = Table(info_data, colWidths=[2.2*inch, 4*inch])
    t.setStyle(TableStyle([
        ("BACKGROUND",     (0,0),(0,-1), C_INDIGO),
        ("TEXTCOLOR",      (0,0),(0,-1), colors.white),
        ("FONTNAME",       (0,0),(-1,-1), "Helvetica"),
        ("FONTSIZE",       (0,0),(-1,-1), 10),
        ("ROWBACKGROUNDS", (1,0),(-1,-1), [C_LIGHT, colors.white]),
        ("GRID",           (0,0),(-1,-1), 0.5, colors.HexColor("#d1d5db")),
        ("PADDING",        (0,0),(-1,-1), 8),
    ]))
    content.append(t)
    content.append(Spacer(1, 14))

    content.append(Paragraph("Matched Skills", heading_s))
    for s in (matched or ["None"]):
        content.append(Paragraph(f"✔ {s}", bullet_s))
    content.append(Spacer(1, 10))

    content.append(Paragraph("Missing Skills", heading_s))
    for s in (missing or ["None"]):
        content.append(Paragraph(f"✖ {s}", bullet_s))
    content.append(Spacer(1, 10))

    content.append(Paragraph("Improvement Suggestions", heading_s))
    suggestions_list = analysis.suggestions.split("|") if analysis.suggestions else []
    for tip in (suggestions_list or ["No suggestions available at the moment."]):
        content.append(Paragraph(f"• {tip}", bullet_s))
    content.append(Spacer(1, 10))

    content.append(Paragraph("Skill Roadmap", heading_s))
    try:
        roadmap_items = json.loads(analysis.roadmap) if analysis.roadmap else []
    except (ValueError, TypeError):
        roadmap_items = []

    if not roadmap_items:
        content.append(Paragraph("No roadmap data available for this analysis.", body_s))
    else:
        for item in roadmap_items:
            skill    = item.get("skill", "Unknown skill")
            priority = item.get("priority", "")
            course   = item.get("course") or "No course recommendation available"
            url      = item.get("course_url", "")
            platform = item.get("platform", "")
            cert     = item.get("cert", "")
            valid_url = bool(url) and (url.startswith("http://") or url.startswith("https://"))

            line = f"<b>{skill}</b>"
            if priority:
                line += f" — {priority} Priority"
            if platform:
                line += f" (via {platform})"
            content.append(Paragraph(line, bullet_s))
            content.append(Paragraph(f"Course: {course}", bullet_s))
            if valid_url:
                content.append(Paragraph(f'Link: <link href="{url}">{url}</link>', bullet_s))
            else:
                content.append(Paragraph("Link: No valid course URL available", bullet_s))
            if cert:
                content.append(Paragraph(f"Certification: {cert}", bullet_s))
            content.append(Spacer(1, 4))

    doc.build(content)
    return FileResponse(pdf_path, filename=f"ATS_Report_{analysis_id}.pdf", media_type="application/pdf")