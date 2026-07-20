from sqlalchemy import Column, Integer, String, Text, ForeignKey, DateTime, Float
from sqlalchemy.orm import relationship
from datetime import datetime
from app.core.database import Base

class Resume(Base):
    __tablename__ = "resumes"
    id             = Column(Integer, primary_key=True, index=True)
    filename       = Column(String(255))
    extracted_text = Column(Text)
    user_id        = Column(Integer, ForeignKey("users.id"), nullable=True)
    ats_score      = Column(Float, default=0.0)
    grade          = Column(String(20), default="")
    detected_domain = Column(String(100), default="")
    uploaded_at    = Column(DateTime, default=datetime.utcnow)
    user           = relationship("User", back_populates="resumes")
    analysis       = relationship("Analysis", back_populates="resume")
