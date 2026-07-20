import json, re, os, urllib.request, urllib.error, time
from dotenv import load_dotenv
load_dotenv()

GROQ_API_KEY = os.getenv("GROQ_API_KEY")
GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"


def _call_groq(prompt, max_tokens=1500):
    if not GROQ_API_KEY:
        raise RuntimeError("GROQ_API_KEY not found in .env file")
    body = json.dumps({
        "model": "llama-3.3-70b-versatile",
        "messages": [
            {"role": "system", "content": "You are an expert ATS resume analyzer. Always respond with valid JSON only. No explanation, no markdown, no code fences."},
            {"role": "user", "content": prompt}
        ],
        "max_tokens": max_tokens,
        "temperature": 0.2,
    }).encode("utf-8")
    req = urllib.request.Request(
        GROQ_URL, data=body,
        headers={"Content-Type": "application/json", "Authorization": f"Bearer {GROQ_API_KEY}", "User-Agent": "Mozilla/5.0", "Accept": "application/json"},
        method="POST"
    )
    for attempt in range(3):
        try:
            with urllib.request.urlopen(req, timeout=30) as resp:
                data = json.loads(resp.read().decode("utf-8"))
                return data["choices"][0]["message"]["content"]
        except urllib.error.HTTPError as e:
            if attempt == 2:
                raise RuntimeError(f"Groq API error {e.code}: {e.read().decode()}")
            time.sleep(2 ** attempt)
        except Exception as e:
            if attempt == 2:
                raise RuntimeError(f"Groq call failed: {e}")
            time.sleep(2 ** attempt)


def _parse_json(text):
    text = re.sub(r"```json\s*", "", text)
    text = re.sub(r"```\s*", "", text).strip()
    try:
        return json.loads(text)
    except Exception:
        match = re.search(r'(\{.*\}|\[.*\])', text, re.DOTALL)
        if match:
            return json.loads(match.group(1))
        raise ValueError("Unable to parse JSON response")


def _safe_float(value, default=0):
    try:
        return float(value)
    except (ValueError, TypeError):
        return default


def _grade_color(grade):
    return {"Excellent": "success", "Good": "primary", "Average": "warning", "Poor": "danger"}.get(grade, "secondary")


def _ensure_list(value):
    """Guarantee a list of strings is returned, regardless of what the LLM sent.
    If the LLM ever returns a plain string instead of a JSON array for a field
    like 'feedback' or 'suggestions', Jinja's `{% for x in value %}` would
    silently iterate character-by-character (since strings are iterable in
    Python) and render one card per letter. Normalize here so that can never
    happen downstream."""
    if isinstance(value, list):
        return [str(item).strip() for item in value if str(item).strip()]
    if isinstance(value, str):
        stripped = value.strip()
        return [stripped] if stripped else []
    return []


def _remove_overlap(matched, missing):
    """Remove any missing_skill that conceptually overlaps with an already-matched skill.
    e.g. if 'JIRA' is matched, remove 'Defect Tracking Tools' / 'Bug Tracking' from missing."""
    matched_lower = [m.lower() for m in matched]
    cleaned_missing = []
    for miss in missing:
        miss_lower = miss.lower()
        is_redundant = False
        for m in matched_lower:
            if m in miss_lower or miss_lower in m:
                is_redundant = True
                break
        if not is_redundant:
            cleaned_missing.append(miss)
    return cleaned_missing


GENERIC_SKILLS = {
    "ms office", "microsoft office", "email", "typing", "communication",
    "teamwork", "time management", "problem solving", "ms word", "ms excel",
    "ms powerpoint", "internet", "basic computer skills",
}


def _normalize(text):
    return re.sub(r"[^a-z0-9+.# ]", " ", text.lower())


def _tokenize_words(text):
    raw_words = re.findall(r"[a-z0-9+.#]+", text.lower())
    cleaned = set()
    for w in raw_words:
        w = w.strip(".")  # drop sentence-ending/leading periods, keep internal dots (e.g. "power.bi" style tools)
        if w:
            cleaned.add(w)
    return cleaned


def _skill_in_text(skill, norm_text, text_words):
    """True if `skill` genuinely appears in the given text (word-boundary aware)."""
    skill_norm = _normalize(skill).strip()
    if not skill_norm:
        return False
    if " " in skill_norm:
        return skill_norm in norm_text
    return skill_norm in text_words


def _verify_against_jd_and_resume(matched, missing, jd_text, resume_text):
    """Cross-check the LLM's matched/missing lists against the actual JD and
    resume text. Only skills genuinely present in the JD are allowed to remain
    in either list; placement between matched/missing is corrected based on
    whether the skill is actually present in the resume."""
    jd_norm = _normalize(jd_text)
    jd_words = _tokenize_words(jd_text)
    resume_norm = _normalize(resume_text)
    resume_words = _tokenize_words(resume_text)

    all_candidates = list(dict.fromkeys(matched + missing))  # dedupe, keep order

    verified_matched, verified_missing = [], []
    for skill in all_candidates:
        # Rule (a): skill must genuinely be part of the JD. If it isn't
        # (e.g. AI hallucinated a resume-only skill like "Git"/"SQL" into
        # matched_skills), drop it entirely — it should never have been
        # compared against the JD in the first place.
        if not _skill_in_text(skill, jd_norm, jd_words):
            continue

        # Rule (b): correctly place it based on real presence in the resume,
        # regardless of what the LLM originally decided.
        if _skill_in_text(skill, resume_norm, resume_words):
            verified_matched.append(skill)
        else:
            verified_missing.append(skill)

    return verified_matched, verified_missing


def _is_generic_only(matched_skills):
    """True if matched_skills has no real signal — empty, or every entry is a generic office/soft skill."""
    if not matched_skills:
        return True
    return all(skill.strip().lower() in GENERIC_SKILLS for skill in matched_skills)


def groq_ats_score(resume_text):
    """Fully AI-driven — Groq decides domain, skills, and score. No hardcoded dictionary."""
    prompt = f"""You are an expert ATS resume analyzer with deep knowledge of every professional domain — Software Development, Software Testing, Data Science, DevOps, Finance, Marketing, HR, Sales, Cybersecurity, Design, etc.

Analyze this resume and:
1. Detect the candidate's primary professional domain accurately (be specific, e.g. "Software Testing/QA" not just "IT")
2. Using your own knowledge of what skills/tools matter for that exact domain, list which key skills the candidate already demonstrates (matched_skills) — use SPECIFIC tool/skill names (e.g. "JIRA", "Selenium", "Python"), never generic categories
3. List important skills for that domain that are missing from the resume (missing_skills) — these must be genuinely relevant to the detected domain only, and must use SPECIFIC tool/skill names, never generic categories
4. Score the resume 0-100 based on skill coverage, depth of experience, and overall resume strength for that domain

CRITICAL RULE: Never list a generic category in missing_skills if a specific tool from that category is already in matched_skills.
- If "JIRA" is matched, do NOT list "Defect Tracking Tools" or "Bug Tracking" as missing
- If "Selenium" is matched, do NOT list "Test Automation Tools" as missing
- If "Python" is matched, do NOT list "Programming Languages" as missing
- If "Excel" is matched, do NOT list "Spreadsheet Tools" as missing
- Always use the most specific name possible for both matched and missing skills

DOMAIN DETECTION RULE — read carefully:
- Domains are NOT limited to software/IT. Manufacturing, Quality Assurance, Healthcare, Finance, Mechanical Engineering, Electrical Engineering, Logistics, Retail, Hospitality, HR, Marketing, etc. are all equally valid, specific domains.
- Assign a specific domain whenever the resume shows evidence relevant to ANY industry — this includes specific machine/equipment names, industry certifications, methodologies, job titles, or tools (e.g. "EPI-2000" machine, "GMP", "Six Sigma", "Lean Manufacturing", "ISO 9001" are strong evidence for a Manufacturing/Production/Quality domain — do NOT treat these as "no domain" just because they aren't software-related).
- Only fall back to detected_domain "General" when the resume genuinely has no industry-specific signal at all — e.g. only generic office/soft skills like "MS Office", "Email", "Typing", "Communication" and nothing else. Do not use "General" just because the domain isn't software/IT.

Return ONLY this JSON, nothing else:
{{"detected_domain":"exact domain name","ats_score":75,"grade":"Good","matched_skills":["skill1","skill2"],"missing_skills":["skill3","skill4"],"summary":"2-3 sentence honest, specific assessment of this resume for this domain"}}

Grade rules: Excellent=80-100, Good=60-79, Average=40-59, Poor=0-39
Max 15 matched_skills, max 8 missing_skills.

Resume:
{resume_text[:3500]}"""
    try:
        result = _parse_json(_call_groq(prompt, 900))
        grade = result.get("grade", "Average")
        matched = _ensure_list(result.get("matched_skills", []))[:15]
        missing = _ensure_list(result.get("missing_skills", []))[:8]
        missing = _remove_overlap(matched, missing)
        detected_domain = result.get("detected_domain", "General")
        if _is_generic_only(matched):
            detected_domain = "General"
        return {
            "detected_domain": detected_domain,
            "ats_score": round(_safe_float(result.get("ats_score", 0)), 1),
            "grade": grade,
            "grade_color": _grade_color(grade),
            "matched_skills": matched,
            "missing_skills": missing,
            "summary": result.get("summary", ""),
        }
    except Exception as e:
        return {"detected_domain": "General", "ats_score": 0, "grade": "Error", "grade_color": "secondary", "matched_skills": [], "missing_skills": [], "summary": f"Analysis failed: {e}"}


def groq_jd_match(resume_text, job_description):
    prompt = f"""You are a strict ATS system. Compare resume against job description.

STRICT RULES:
1. Extract ALL skills/tools/technologies mentioned in the JD
2. matched_skills = skills from JD that ARE present in the resume (read resume carefully line by line)
3. missing_skills = skills from JD that are NOT present in the resume
4. NEVER add any skill not explicitly written in the JD
5. If skill is in resume AND in JD, it MUST go in matched_skills
6. Double check resume before marking any skill as missing
7. Use SPECIFIC tool/skill names only, never generic categories
8. CRITICAL: If a specific tool is in matched_skills (e.g. "JIRA"), do NOT also list its generic category in missing_skills (e.g. "Defect Tracking Tools")
9. You MUST always return at least 4 items in "suggestions", even if match_percentage is high — give concrete, specific advice for this exact resume and JD, never leave it empty and never return fewer than 4.
10. CRITICAL — DO NOT SKIP ANY JD SKILL: Even if a skill/tool/term explicitly named in the JD looks unusual, unfamiliar, made-up, or is not a well-known real-world tool, you MUST still extract it and place it in matched_skills (if present in resume) or missing_skills (if not). NEVER silently drop an explicitly-stated JD requirement just because you don't recognize it — every skill named in the JD must end up in exactly one of the two lists.

Return ONLY this JSON:
{{"match_percentage":72.5,"matched_skills":["selenium","postman","sql","git"],"missing_skills":["docker"],"feedback":["specific observation about this resume for this role"],"suggestions":["specific actionable tip 1","specific actionable tip 2","specific actionable tip 3","specific actionable tip 4"]}}

=== JD (extract skills only from here) ===
{job_description[:1500]}

=== RESUME (check which JD skills are present here) ===
{resume_text[:2500]}"""
    try:
        result = _parse_json(_call_groq(prompt, 1400))
        matched = _ensure_list(result.get("matched_skills", []))
        missing = _ensure_list(result.get("missing_skills", []))
        matched, missing = _verify_against_jd_and_resume(matched, missing, job_description, resume_text)
        missing = _remove_overlap(matched, missing)

        suggestions = _ensure_list(result.get("suggestions", []))
        # BUG_022 FIX: guarantee at least 4 suggestions even if the LLM
        # under-delivers, so the PDF report's Improvement Tips section is
        # never short. Generic fallbacks are appended only to fill the gap,
        # real AI-generated ones are always kept first.
        generic_fallbacks = [
            "Tailor your resume keywords to more closely match the exact terminology used in the job description.",
            "Quantify your achievements with specific numbers, percentages, or metrics wherever possible.",
            "Ensure your most relevant skills for this role are prominently featured near the top of your resume.",
            "Consider adding a brief summary section that directly addresses the key requirements of this job description.",
        ]
        for fallback in generic_fallbacks:
            if len(suggestions) >= 4:
                break
            if fallback not in suggestions:
                suggestions.append(fallback)

        return {
            "match_percentage": round(_safe_float(result.get("match_percentage", 0)), 1),
            "matched_skills": matched,
            "missing_skills": missing,
            "feedback": _ensure_list(result.get("feedback", [])),
            "suggestions": suggestions,
        }
    except Exception as e:
        return {"match_percentage": 0.0, "matched_skills": [], "missing_skills": [], "feedback": [f"Analysis failed: {e}"], "suggestions": []}


def groq_rewrite_bullets(resume_text):
    prompt = f"""Find complete bullet points (minimum 8 words) in this resume that use WEAK language: worked on, helped, assisted, handled, used, made, did, was responsible for, participated in.

CRITICAL RULES:
1. Do NOT select bullets that already start with a strong action verb (e.g. "Developed", "Optimized", "Delivered", "Led", "Architected", "Engineered", "Implemented", "Built", "Designed", "Managed", "Collaborated", "Reduced", "Increased"). Bullets already using strong action verbs are NOT weak — skip them entirely, even if they could theoretically be phrased differently.
2. When rewriting, you MUST preserve only the facts, numbers, and metrics that are EXPLICITLY present in the original sentence. NEVER invent, estimate, or add any new number, percentage, count, or achievement that is not already stated in the original bullet — this is strictly forbidden even if it makes the bullet sound more impressive.
3. Only replace the weak verb/phrasing with a stronger one and improve sentence structure — the factual content (what was done, what tools, what numbers) must remain exactly as stated in the original.
4. If a bullet has no weak language, do NOT include it in the output at all.

Rewrite each qualifying weak bullet with a strong action verb, preserving only the original facts.
Return ONLY JSON array:
[{{"original":"complete original sentence minimum 8 words","improved":"complete rewritten sentence minimum 8 words, same facts only"}}]
Max 4 items. Return [] if none found.
Resume:
{resume_text[:2500]}"""
    try:
        result = _parse_json(_call_groq(prompt, 800))
        if isinstance(result, list):
            return [r for r in result
                    if len(r.get("original","").split()) >= 8
                    and len(r.get("improved","").split()) >= 8
                    and r.get("original","").strip().lower() != r.get("improved","").strip().lower()][:4]
        return []
    except Exception:
        return []


_PLACEHOLDER_PATTERNS = (
    "no relevant", "not found", "not available", "n/a", "none available",
    "no course", "no certification", "no cert", "unavailable",
)


def _is_placeholder(text):
    """True if the LLM sent a 'there's nothing here' sentence instead of
    real content (or an actual None/empty value). Used to sanitize course/
    cert fields so downstream templates/PDF can rely on a simple truthy
    check without accidentally displaying the LLM's own placeholder
    sentence as if it were real data."""
    if not text or not isinstance(text, str):
        return True
    lowered = text.strip().lower()
    if not lowered:
        return True
    return any(pattern in lowered for pattern in _PLACEHOLDER_PATTERNS)


def _is_useless_url(url):
    """True if the URL passes a basic http(s) check but is actually a bare
    search-engine homepage with no query string (e.g. the LLM returning
    'https://www.google.com/search' with nothing after it). Such a link is
    technically valid but leads nowhere useful for the user."""
    if not url or not isinstance(url, str):
        return True
    url_lower = url.lower()
    bare_search_domains = ("google.com/search", "bing.com/search", "duckduckgo.com")
    looks_like_search_engine = any(domain in url_lower for domain in bare_search_domains)
    has_query = "?" in url or "q=" in url_lower
    return looks_like_search_engine and not has_query


def groq_skill_roadmap(missing_skills, domain):
    if not missing_skills:
        return []
    skills_list = missing_skills[:8]
    prompt = f"""A {domain} professional is missing exactly these skills: {", ".join(skills_list)}

For EACH skill listed above, create exactly ONE roadmap item recommending a course/certification for THAT SPECIFIC skill. Do not substitute a different skill, do not skip any skill, and do not invent extra skills not in the list.

CRITICAL RULES:
1. The "skill" field in each roadmap item MUST be an exact match (or very close variant) of one of the skills listed above — never a different/unrelated skill.
2. The recommended course/cert must be genuinely and specifically relevant to that exact skill and the {domain} domain (e.g. for "Six Sigma" recommend an actual Six Sigma certification, not an unrelated tool course).
3. Return exactly {len(skills_list)} items, one per input skill, in the same order as listed above.

Return ONLY JSON array:
[{{"skill":"power bi","priority":"High","course":"Microsoft Power BI Desktop","course_url":"https://www.udemy.com/course/microsoft-power-bi-up-running-with-power-bi-desktop/","platform":"Udemy","cert":"PL-300 Microsoft Power BI Data Analyst"}}]
Priority: High=first 3, Medium=next 3, Low=rest. Real URLs only."""
    try:
        result = _parse_json(_call_groq(prompt, 1000))
        if not isinstance(result, list):
            return []

        # BUG FIX (roadmap-skill-mapping): don't blindly trust the LLM's
        # "skill" field. It sometimes ignores the requested missing_skills
        # list and returns unrelated/generic skills (e.g. Jira, Selenium,
        # DevOps) instead of a card for the skill that's actually missing.
        # Only accept an item if it genuinely corresponds to one of the
        # skills we asked about, and make sure every requested skill still
        # gets a card even if the LLM skipped it.
        normalized_requested = {s.strip().lower(): s for s in skills_list}

        seen, unique = set(), []
        for item in result:
            key = str(item.get("skill", "")).strip().lower()
            matched_skill = None
            for req_norm, req_original in normalized_requested.items():
                if req_norm == key or req_norm in key or key in req_norm:
                    matched_skill = req_original
                    break
            if not matched_skill or matched_skill.lower() in seen:
                continue
            seen.add(matched_skill.lower())
            item["skill"] = matched_skill  # normalize to the actual requested skill name
            unique.append(item)

        # Any requested skill the LLM didn't map to (skipped or replaced with
        # something unrelated) still gets a graceful fallback card instead of
        # silently disappearing.
        for skill in skills_list:
            if skill.lower() not in seen:
                unique.append({
                    "skill": skill,
                    "priority": "Medium",
                    "course": f"Search for '{skill}' courses/certifications",
                    "course_url": f"https://www.google.com/search?q={skill.replace(' ', '+')}+course+certification",
                    "platform": "Web Search",
                    "cert": None,
                })

        # Sanitize placeholder "there's nothing here" sentences the LLM
        # sometimes sends instead of a real null/empty value (e.g.
        # "No relevant certification found"), and fix "bare" search-engine
        # URLs that pass a scheme check but lead nowhere useful. Without
        # this, both the PDF and the web UI would display the LLM's
        # placeholder sentence as if it were real course/cert data, or a
        # dead-end link as if it were a working course URL.
        for item in unique:
            if _is_placeholder(item.get("course")):
                item["course"] = "No course recommendation available"
            if _is_placeholder(item.get("cert")):
                item["cert"] = None
            if _is_useless_url(item.get("course_url")):
                skill_name = item.get("skill", "this skill")
                item["course_url"] = f"https://www.google.com/search?q={skill_name.replace(' ', '+')}+course+certification"

        return unique[:8]
    except Exception:
        return []