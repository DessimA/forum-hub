# 06 - Testing Strategy

## 🧪 Methodology
We adopt a rigorous **TDD (Test Driven Development)** approach. System stability is guaranteed by a suite covering everything from business rules to security.

## 📊 Implemented Testing Levels

### Backend (Java)
- **Unit:** Extensive coverage of `Services` and `Mappers` using **Mockito**.
- **Integration:** Validation of complex flows using **H2 database in MySQL mode**.
- **Security:** Specific tests to validate user blocking (`SuspendedUserTest`) and permission hierarchy.
- **Auditing:** Data recovery tests ensure that UUID-based restoration logic works perfectly.

### Frontend (Vue.js)
- **Unit:** Pinia Store tests using **Vitest**.
- **Component:** UI and event validation with **Vue Test Utils**.
- **Typing:** Static verification via **TypeScript** integrated into the build process to prevent runtime errors.

## 🚀 Automated Execution
We use Docker to ensure tests run in an environment identical to production:
`./automation/test.sh`

## 📈 Quality Metrics
- **Backend Coverage:** > 80% (focused on critical logic).
- **Frontend Coverage:** > 75% (focused on store states and layout components).