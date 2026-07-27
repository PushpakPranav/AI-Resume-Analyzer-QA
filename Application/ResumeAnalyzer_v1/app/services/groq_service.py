import json, re, os, urllib.request, urllib.error, time
from dotenv import load_dotenv
load_dotenv()

GROQ_API_KEY = os.getenv("GROQ_API_KEY")
GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"


class GroqTruncatedError(Exception):
    def __init__(self, partial_content):
        self.partial_content = partial_content
        super().__init__("Groq response was truncated (hit max_tokens)")


def _build_groq_request(prompt, max_tokens, temperature, seed):
    body = json.dumps({
        "model": "llama-3.3-70b-versatile",
        "messages": [
            {"role": "system", "content": "You are an expert ATS resume analyzer. Always respond with valid JSON only. No explanation, no markdown, no code fences."},
            {"role": "user", "content": prompt}
        ],
        "max_tokens": max_tokens,
        "temperature": temperature,
        "seed": seed,
    }).encode("utf-8")
    return urllib.request.Request(
        GROQ_URL, data=body,
        headers={"Content-Type": "application/json", "Authorization": f"Bearer {GROQ_API_KEY}", "User-Agent": "Mozilla/5.0", "Accept": "application/json"},
        method="POST"
    )


def _call_groq(prompt, max_tokens=1500, temperature=0.1, seed=42):
    if not GROQ_API_KEY:
        raise RuntimeError("GROQ_API_KEY not found in .env file")

    for attempt in range(3):
        req = _build_groq_request(prompt, max_tokens, temperature, seed)
        try:
            with urllib.request.urlopen(req, timeout=30) as resp:
                data = json.loads(resp.read().decode("utf-8"))
                choice = data["choices"][0]
                content = choice["message"]["content"]
                if choice.get("finish_reason") == "length":
                    raise GroqTruncatedError(content)
                return content
        except GroqTruncatedError:
            if attempt == 2:
                raise
            max_tokens = int(max_tokens * 1.5)
            time.sleep(1)
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


def _call_groq_json(prompt, max_tokens=1500, temperature=0.1, seed=42, max_retries=2):
    last_err = None
    for attempt in range(max_retries + 1):
        try:
            raw = _call_groq(prompt, max_tokens=max_tokens, temperature=temperature, seed=seed)
            return _parse_json(raw)
        except Exception as e:
            last_err = e
            if attempt < max_retries:
                time.sleep(1)
                continue
            raise last_err


def _safe_float(value, default=0):
    try:
        return float(value)
    except (ValueError, TypeError):
        return default


def _grade_color(grade):
    return {"Excellent": "success", "Good": "primary", "Average": "warning", "Poor": "danger"}.get(grade, "secondary")


def _ensure_list(value):
    if isinstance(value, list):
        return [str(item).strip() for item in value if str(item).strip()]
    if isinstance(value, str):
        stripped = value.strip()
        return [stripped] if stripped else []
    return []


def _remove_overlap(matched, missing):
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
        w = w.strip(".")
        if w:
            cleaned.add(w)
    return cleaned


def _skill_in_text(skill, norm_text, text_words):
    skill_norm = _normalize(skill).strip()
    if not skill_norm:
        return False
    if " " not in skill_norm:
        return skill_norm in text_words
    if skill_norm in norm_text:
        return True
    core_tokens = [t for t in skill_norm.split() if t not in ("ms", "microsoft")]
    if not core_tokens:
        core_tokens = skill_norm.split()
    return all(t in text_words for t in core_tokens)


def _verify_against_jd_and_resume(matched, missing, jd_text, resume_text):
    jd_norm = _normalize(jd_text)
    jd_words = _tokenize_words(jd_text)
    resume_norm = _normalize(resume_text)
    resume_words = _tokenize_words(resume_text)

    all_candidates = list(dict.fromkeys(matched + missing))

    verified_matched, verified_missing = [], []
    for skill in all_candidates:
        if not _skill_in_text(skill, jd_norm, jd_words):
            continue
        if _skill_in_text(skill, resume_norm, resume_words):
            verified_matched.append(skill)
        else:
            verified_missing.append(skill)

    return verified_matched, verified_missing


def _is_generic_only(matched_skills):
    if not matched_skills:
        return True
    return all(skill.strip().lower() in GENERIC_SKILLS for skill in matched_skills)


def _dedupe_skill_list(skills):
    result = []
    for s in skills:
        s_stripped = str(s).strip()
        if not s_stripped:
            continue
        low = s_stripped.lower()
        if any(low in r.lower() or r.lower() in low for r in result):
            continue
        result.append(s_stripped)
    return result


def groq_ats_score(resume_text):
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
        result = _call_groq_json(prompt, max_tokens=900)
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
            "error": False,
        }
    except Exception:
        return {
            "detected_domain": "General",
            "ats_score": 0,
            "grade": "Error",
            "grade_color": "secondary",
            "matched_skills": [],
            "missing_skills": [],
            "summary": "We couldn't fully analyze this resume due to a temporary issue. Please try again.",
            "error": True,
        }


def groq_jd_match(resume_text, job_description):
    prompt = f"""You are a strict ATS system analyzing a job description.

TASK: Extract EVERY skill, tool, technology, or specific competency that is explicitly required or mentioned in the JD below. This is extraction only — do not check the resume yet.

RULES:
1. List EVERY skill/tool named in the JD, no matter how minor or how it's phrased (e.g. "MS Word", "Outlook", "Data Entry", "VLOOKUP", "Pivot Table") — never skip one just because it's mentioned briefly or alongside another skill (e.g. "MS Word and Outlook" contains TWO separate skills: "MS Word" AND "Outlook" — both must be listed).
2. Use SPECIFIC tool/skill names, never generic categories (e.g. "MS Outlook" not "office tools", "Python" not "programming languages").
3. Do not invent a skill that is not explicitly stated in the JD.
4. Also write a 1-2 sentence feedback note and at least 4 specific, actionable suggestions for this resume given this JD.

Return ONLY this JSON:
{{"jd_required_skills":["ms excel","ms word","ms outlook","vlookup","pivot table","data entry"],"feedback":["specific observation about this resume for this role"],"suggestions":["specific actionable tip 1","specific actionable tip 2","specific actionable tip 3","specific actionable tip 4"]}}

=== JOB DESCRIPTION (extract every skill named here) ===
{job_description[:1500]}

=== RESUME (context only, for feedback/suggestions — do not use it to decide which skills to list) ===
{resume_text[:2500]}"""
    try:
        result = _call_groq_json(prompt, max_tokens=2000)
        jd_skills = _dedupe_skill_list(_ensure_list(result.get("jd_required_skills", [])))

        # Classification is deterministic, not left to the LLM: every
        # extracted JD skill is checked directly against the resume text.
        # This guarantees every explicit JD skill lands in exactly one of
        # matched/missing, instead of relying on the model to also get the
        # matched/missing split right in the same pass.
        matched, missing = _verify_against_jd_and_resume(jd_skills, [], job_description, resume_text)
        missing = _remove_overlap(matched, missing)

        suggestions = _ensure_list(result.get("suggestions", []))
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

        total_skills = len(matched) + len(missing)
        if total_skills == 0:
            match_percentage = 0.0
        else:
            match_percentage = round((len(matched) / total_skills) * 100, 1)

        return {
            "match_percentage": match_percentage,
            "matched_skills": matched,
            "missing_skills": missing,
            "feedback": _ensure_list(result.get("feedback", [])),
            "suggestions": suggestions,
            "error": False,
        }
    except Exception:
        return {
            "match_percentage": 0.0,
            "matched_skills": [],
            "missing_skills": [],
            "feedback": ["We couldn't fully analyze this JD due to a temporary issue. Please try again."],
            "suggestions": [],
            "error": True,
        }


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
        result = _call_groq_json(prompt, max_tokens=800)
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
    if not text or not isinstance(text, str):
        return True
    lowered = text.strip().lower()
    if not lowered:
        return True
    return any(pattern in lowered for pattern in _PLACEHOLDER_PATTERNS)


def _is_useless_url(url):
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
        result = _call_groq_json(prompt, max_tokens=1000)
        if not isinstance(result, list):
            return []

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
            item["skill"] = matched_skill
            unique.append(item)

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