# AI Resume Analyzer — QA Project

An AI-powered web application that analyzes resumes against job descriptions — calculating an ATS score, detecting the candidate's domain, matching/missing skills, generating an AI summary, and producing a personalized skill roadmap. This repository contains both the **application** and the **full QA suite** (manual + automation) built around it.

---

## Project Structure

```
AI-ResumeAnalyzer-QA/
├── Application/
│   └── ResumeAnalyzer_v1/        # FastAPI web application
│       ├── app/
│       │   ├── core/              # DB connection & security (JWT/hashing)
│       │   ├── models/            # SQLAlchemy models (User, Resume, Analysis)
│       │   ├── routes/            # auth, resume, analysis, dashboard, profile
│       │   ├── services/          # Groq AI service, resume parser
│       │   ├── static/            # CSS, JS, avatars
│       │   └── templates/         # Jinja2 + Bootstrap 5 templates
│       ├── main.py
│       ├── requirements.txt
│       └── .env.example
├── Automation/                    # Selenium + TestNG automation framework
│   ├── src/main/java/             # Page Objects, Driver Factory, components
│   ├── src/test/java/             # Test classes, base test, listeners
│   ├── src/test/resources/        # config.properties, test data, log4j2
│   └── pom.xml
├── Documentation/
│   ├── FRS/                       # Functional Requirement Specification
│   ├── TestArtifacts/             # Test scenarios & test cases
│   ├── BugReports/                # Logged bugs
│   └── SecurityTesting/           # Security test cases & findings
└── README.md
```

---

## Tech Stack

**Application**
- Python, FastAPI, Jinja2
- SQLAlchemy + MySQL
- Groq AI (Llama 3.3 70B) for resume analysis
- ReportLab (PDF report generation)
- Bootstrap 5

**Automation**
- Java, Selenium WebDriver, TestNG, Maven

---

## Getting Started — Application

1. **Navigate to the app folder**
   ```bash
   cd Application/ResumeAnalyzer_v1
   ```

2. **Create a virtual environment & install dependencies**
   ```bash
   python -m venv venv
   source venv/bin/activate      # Windows: venv\Scripts\activate
   pip install -r requirements.txt
   ```

3. **Configure environment variables**

   Copy `.env.example` to `.env` and fill in your own values:
   ```bash
   cp .env.example .env
   ```
   ```env
   MYSQL_HOST=localhost
   MYSQL_PORT=3306
   MYSQL_USER=root
   MYSQL_PASSWORD=your_mysql_password
   MYSQL_DATABASE=resume_analyzer
   SECRET_KEY=change_this_to_a_random_secret_key
   GROQ_API_KEY=your_groq_api_key_here
   ```

4. **Create the MySQL database**
   ```sql
   CREATE DATABASE resume_analyzer;
   ```

5. **Run the app**
   ```bash
   uvicorn main:app --reload
   ```
   The app will be available at `http://127.0.0.1:8000`

---

## Getting Started — Automation (QA Suite)

1. **Navigate to the automation folder**
   ```bash
   cd Automation
   ```

2. **Update config** — edit `src/test/resources/config.properties` if the app URL/browser differs from defaults.

3. **Run tests with Maven**
   ```bash
   mvn clean test
   ```

Reports are generated in `Automation/reports/` and screenshots (on failure) in `Automation/screenshots/`.

---

## My QA Responsibilities

- Requirement Analysis
- Functional Requirement Specification (FRS)
- Test Scenario Creation
- Test Case Design
- Manual Testing
- API Testing
- Database Testing
- Security Testing
- Selenium Automation Testing
- Bug Reporting

---

## Author

**Pushpak Pranav**
QA Engineer
