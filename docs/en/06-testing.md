# 06 - Testing Strategy

## 🧪 TDD (Test Driven Development)
In this project, no code was implemented without first having a test to validate its expected behavior.

## 📊 Testing Levels

### Backend
1. **Unit (JUnit + Mockito):** Tests business logic in isolation within Services.
2. **Integration (H2):** Tests database communication and security filters.
3. **E2E (TestContainers):** Tests the full flow using a real MySQL container.

### Frontend
1. **Unit (Vitest):** Tests Stores and utility functions.
2. **Component (Vue Test Utils):** Tests if components render and react to events correctly.
3. **E2E (Cypress):** Simulates a real user navigating and interacting with the UI.
