# AI Resume Analyzer — QA Project

An AI-powered web application that analyzes resumes against job descriptions — calculating an ATS score, detecting the candidate's domain, matching/missing skills, generating an AI summary, and producing a personalized skill roadmap. This repository contains both the **application** and the **full QA suite** (manual + automation) built around it.

---

## Project Structure

```
AI-ResumeAnalyzer-QA/
├── Application/
│   └── ResumeAnalyzer_v1/        # FastAPI web application
│       ├── app/
│       │   ├── core/              # DB connection & security (JWT/hashing/CSRF/lockout)
│       │   ├── models/            # SQLAlchemy models (User, Resume, Analysis)
│       │   ├── routes/            # auth, resume, analysis, dashboard, profile
│       │   ├── services/          # Groq AI service, resume parser
│       │   ├── static/            # CSS, JS, avatars
│       │   └── templates/         # Jinja2 + Bootstrap 5 templates
│       ├── main.py
│       ├── requirements.txt
│       └── .env.example
├── Automation/                    # Selenium + TestNG automation framework
│   ├── src/main/java/             # Page Objects, components, DriverFactory, WaitUtils
│   ├── src/test/java/
│   │   ├── tests/                 # 21 test classes (211 tests)
│   │   ├── base/                  # BaseTest — generic driver/config setup
│   │   ├── constants/             # TestData — centralized test data & expected values
│   │   ├── listeners/             # ScreenshotListener — auto-capture on failure
│   │   └── apitests/              # REST-assured API tests (separate scope)
│   ├── src/test/resources/        # config.properties, test data files, log4j2
│   ├── testng.xml                 # Full regression suite (211 tests)
│   ├── testng-smoke.xml           # Smoke suite (24 critical-path tests)
│   └── pom.xml
├── Documentation/
│   ├── FRS/                       # Functional Requirement Specification
│   ├── TestArtifacts/             # Test scenarios & test cases
│   ├── BugReports/                # Logged bugs (BugReport.xlsx)
│   └── SecurityTesting/           # Security test cases & findings (SecurityTesting.xlsx)
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
- Java, Selenium WebDriver 4.33, TestNG 7.11, Maven
- REST-assured (API layer — separate scope, see *apitests*)

---

## Framework Architecture

Standard **Page Object Model**, layered as:

- **`pages/`** — one class per page/screen. All extend `BasePage`, which centralizes common Selenium actions (`click`, `type`, `getText`, `getAttribute`) and wait helpers — every one of these already waits internally, so page objects never call raw Selenium methods directly.
- **`components/`** — reusable page fragments shared across multiple pages (e.g. `NavbarComponent`), following the same pattern as `pages/`.
- **`utils/WaitUtils`** — a single source of truth for explicit waits, built on `WebDriverWait`. Configured to ignore `StaleElementReferenceException` during polling (not just `NoSuchElementException`, which Selenium ignores by default) — this closes a real class of race conditions around page transitions. Offers both a default (10s) and a longer (30s+) timeout overload for pages backed by a slow external dependency (the Groq AI call behind ATS scoring and JD matching).
- **`base/BaseTest`** — deliberately generic: driver init, config load, teardown. No test-specific logic lives here.
- **No `Thread.sleep()` anywhere in the codebase.** All waits are explicit and condition-based.
- **`listeners/ScreenshotListener`** — a TestNG `ITestListener` registered via `@Listeners` on `BaseTest` (so it's inherited everywhere, regardless of which suite file runs). Captures a screenshot to `Automation/screenshots/` automatically on any test failure.

**A deliberate architectural note on AI-dependent pages:** the ATS-result and JD-match-result pages render in a single synchronous server response that blocks on a live Groq API call. Every `isXxxDisplayed()`-style check on those two page objects funnels through one private `isVisible(String id)` helper (30s timeout) rather than each method rolling its own wait — this was a real consistency gap found and fixed during framework hardening, and is the main reason the suite is flake-resistant today.

---

## Testing Scope

| Area | Coverage |
|---|---|
| Authentication | Register, login, logout, forgot/reset password (full flow incl. dev-mode reset link), brute-force lockout |
| Dashboard | Resume upload, history, avatar upload, delete resume, navigation |
| Resume Analysis | ATS scoring, domain detection, JD matching, AI summary/suggestions, PDF report download |
| Security | IDOR (cross-user data access), CSRF (forged token rejection), login lockout, SQL injection regression, XSS regression |
| Validation / Edge Cases | Oversized/empty/wrong-type file uploads, whitespace-only job descriptions, email/password format rules |
| Unauthorized access | Direct URL access to protected routes without a session |

**211 total UI tests** across 21 test classes, run via `testng.xml`. A **24-test smoke subset** (`testng-smoke.xml`, `@Test(groups={"smoke"})`) covers the critical end-to-end path — register → login → dashboard → upload → ATS score → JD match — for a fast sanity check without running the full suite.

API-level tests (`apitests` package, REST-assured) are a **separate, intentionally out-of-scope** concern for this Selenium suite and are excluded from both `testng.xml` and `testng-smoke.xml`.

---

## Security Testing

Security coverage isn't an afterthought here — several of these regression tests exist specifically because a vulnerability was found, reported, fixed at the application level, and then locked in with an automated check so it can't silently regress:

- **IDOR (`IDORAccessTest`)** — confirms a user cannot view another user's analysis history or download another user's PDF report by guessing/incrementing IDs.
- **CSRF (`CSRFProtectionTest`)** — tampers the CSRF token on the login and resume-delete forms via JS and confirms the server rejects the request.
- **Brute-force lockout (`LoginLockoutTest`)** — confirms the account locks after `MAX_LOGIN_ATTEMPTS` failed logins, and — critically — that the lockout also blocks the *correct* password once triggered, not just repeated wrong ones.
- **SQL injection / XSS regression (`SecurityRegressionTest`)** — confirms injection payloads in the login fields and script tags in the register name field are safely neutralized (parameterized queries via SQLAlchemy ORM, Jinja2 auto-escaping).
- **Unauthorized access (`UnauthorizedAccessTest`)** — confirms protected routes redirect rather than render when hit directly without a session.

See `Documentation/SecurityTesting/SecurityTesting.xlsx` and `Documentation/BugReports/BugReport.xlsx` for the original manual findings these regression tests trace back to.

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

2. **Update config** — edit `src/test/resources/config.properties` if the app URL differs from the default (`http://127.0.0.1:8000`). The application must be running before tests start.

3. **Run tests with Maven**

```bash
   mvn test              # Full regression — all 211 UI tests (testng.xml)
   mvn test -P smoke      # Smoke suite — 24 critical-path tests (testng-smoke.xml)
```

Reports are generated in `Automation/reports/` and `Automation/test-output/`. Screenshots on failure are saved automatically to `Automation/screenshots/`.

---

## TestNG Execution

Two suite files, both wired through Maven profiles in `pom.xml`:

- **`testng.xml`** (default, `mvn test`) — all 21 test classes, organized into four logical groups: Smoke and Authentication, Dashboard and Resume Management, Resume Analysis, Security. Sequential execution only — parallel is intentionally **not** enabled, because the Groq AI API has a rate limit that concurrent runs would hit almost immediately.
- **`testng-smoke.xml`** (`mvn test -P smoke`) — the subset of tests tagged `@Test(groups={"smoke"})` across 6 core classes, for a quick pre-check before running the full suite.

Both `@BeforeMethod`/`@BeforeClass` setup methods are annotated `alwaysRun = true`, which is required for TestNG's group filtering to still invoke setup/teardown correctly when only some methods in a class carry the `smoke` group.

---

## Bug Tracking

Manual test cases and logged bugs live in `Documentation/BugReports/BugReport.xlsx` and `Documentation/SecurityTesting/SecurityTesting.xlsx`. Several previously-logged issues now have dedicated automated regression coverage so they can't silently reappear:

| Area | Originally found via | Now regression-tested by |
|---|---|---|
| No account lockout on repeated failed logins | Manual security testing | `LoginLockoutTest` |
| Missing CSRF protection on login / resume delete | Manual security testing | `CSRFProtectionTest` |
| Cross-user access to resume history / reports (IDOR) | Manual security testing | `IDORAccessTest` |
| Oversized / empty / wrong-type file uploads not rejected server-side | Manual functional testing | `FileUploadValidationTest` |

---

## Known Limitations

- **API rate limits.** Groq's API has a request-rate ceiling, which is why the suite runs sequentially rather than in parallel, and why a handful of AI-dependent assertions (progress bar, suggestions card) can legitimately be skipped rather than failed — the framework detects a Groq failure/rate-limit signature in the response body and raises a `SkipException` with a clear message instead of a misleading assertion failure.
- **AI response variability.** The `suggestions` section of a JD-match result is conditionally rendered by the server only when Groq's response includes non-empty suggestions for that particular resume/JD pair — this is expected application behavior, not a test defect.
- **Resume-filename XSS is not covered on Windows.** Windows (NTFS) disallows `<`, `>`, and `"` in filenames at the OS level, so a payload like `<script>...</script>.pdf` cannot be created as a test asset on a Windows machine. This would be testable on Linux/macOS.
- **API tests (`apitests` package) are out of scope for this Selenium suite** and are excluded from both `testng.xml` and `testng-smoke.xml` by design.

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