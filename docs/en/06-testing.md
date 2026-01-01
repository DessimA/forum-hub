# 06 - Automated Testing

**FórumHub** uses a comprehensive testing pyramid, ensuring stability for both the Backend and Frontend.

## 🏛️ Testing Strategy

### Backend (Java/Spring Boot)
- **Unit Tests:** Business logic validation in isolation (JUnit 5 + Mockito).
- **Integration Tests:** Validation of persistence (H2 Database) and security layer.
- **Infrastructure Tests:** Real integration tests with **Redis** to validate Rate Limiting and L2 Cache.

### Frontend (Vue.js)
- **Unit Tests:** Component and store tests (Vitest).
- **E2E (End-to-End):** Critical user flows (Cypress).

---

## 🚀 Execution via Docker (Recommended)

The project has an isolated testing environment that automatically starts the necessary services (Redis + H2).

### Run all tests
```bash
./automation/test.sh
```

### What the script does:
1. Starts a dedicated **Redis** container for testing.
2. Runs backend tests in the `backend-test` container (uses `test` profile).
3. Runs frontend unit tests in the `frontend-test` container.
4. Generates a consolidated report in the terminal.

---

## 🛠️ Testing Infrastructure Details

### Test Profile (`application-test.yml`)
- **Database:** H2 Database (In-Memory with MySQL mode).
- **Cache:** Real Redis (connected via internal Docker network).
- **Rate Limit:** Adjusted to lower limits in specific tests for fast validation.

### Redis Resilience
Integration tests use the `RestPage` utility to validate correct serialization in Redis. If Redis is offline, the system enters an automatic fallback mode, logging a warning but allowing the application to continue functioning.

---

## 📊 Coverage and Quality
- **Backend Target:** > 80% coverage.
- **Frontend Target:** > 75% coverage.
- **Golden Rule:** No code is integrated into the `main` branch without passing 100% of the automated tests.
