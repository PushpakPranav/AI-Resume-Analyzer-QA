from sqlalchemy import Column, Integer, String, Text, ForeignKey, DateTime, Float
from sqlalchemy.orm import relationship
from datetime import datetime
from app.core.database import Base

class Analysis(Base):
    __tablename__ = "analysis"
    id               = Column(Integer, primary_key=True, index=True)
    resume_id        = Column(Integer, ForeignKey("resumes.id"))
    job_description  = Column(Text)
    match_percentage = Column(Float, default=0.0)
    matched_skills   = Column(Text)
    missing_skills   = Column(Text)
    feedback         = Column(Text, nullable=True)
    suggestions      = Column(Text, nullable=True)
    roadmap          = Column(Text, nullable=True)  # JSON-serialized list of roadmap items
    created_at       = Column(DateTime, default=datetime.utcnow)
    resume           = relationship("Resume", back_populates="analysis")