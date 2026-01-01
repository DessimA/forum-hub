# 08 - Future Improvements Roadmap

This document lists the features and refinements planned for ForumHub that have not yet been implemented.

## 🚀 Future Roadmap (Technical Debt & Innovation)

### High Priority

1. **Rate Limiting (Brute-Force Protection)**
   - **Goal:** Implement request limiting per IP, especially on authentication endpoints (`/auth/login`), using Bucket4j or Redis.
   - **Current State:** Absent. The API is vulnerable to massive dictionary attacks.

2. **Frontend Internationalization (Vue-i18n)**
   - **Goal:** Allow the user interface (buttons, menus, labels) to switch languages dynamically between PT-BR and EN.
   - **Current State:** Interface has hardcoded Portuguese texts.

### Medium Priority

3. **Structured Logging (JSON)**
   - **Goal:** Configure Logback to output logs in JSON format in production environments.
   - **Motivation:** Facilitates ingestion and analysis in observability tools (ELK Stack, Datadog, CloudWatch).
   - **Current State:** Standard plain text logs (Console).

4. **RFC 7807 Standardization (Problem Details)**
   - **Goal:** Adopt the official "Problem Details for HTTP APIs" standard for error responses.
   - **Current State:** Custom error format.

### Low Priority

5. **Second Level Cache (L2 Cache)**
   - **Goal:** Implement cache for non-volatile entities (e.g., `Course`, `Profile`) using Caffeine or Redis.
   - **Motivation:** Reduce database load on frequent listings.

6. **CI/CD Pipeline (GitHub Actions)**
   - **Goal:** Automate tests and build on every push.
   - **Current State:** Manual scripts (`run.sh`).