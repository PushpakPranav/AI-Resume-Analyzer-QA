from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import relationship
from app.core.database import Base

class User(Base):
    __tablename__ = "users"
    id             = Column(Integer, primary_key=True, index=True)
    name           = Column(String(100))
    email          = Column(String(150), unique=True, index=True)
    hashed_password = Column(String(255))
    resumes        = relationship("Resume", back_populates="user")
    _avatar = Column("avatar", String(255), nullable=True)
    @property
    def avatar(self):
        return self._avatar or self.name[0].upper() if self.name else "U"

    @avatar.setter  
    def avatar(self, value):
        self._avatar = value