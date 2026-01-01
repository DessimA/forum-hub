# 06 - Testing Strategy Detail

ForumHub uses a robust testing pyramid to ensure business rule stability, data security, and interface integrity.

## 🏗️ Backend (Java & Spring Boot)

### 1. Security & Authentication Tests
Located in `com.forumhub.security.*`.
- **Active Session vs Blocking:** Ensures immediate access revocation for blocked users.
- **Token Validation:** Validates JWT integrity, expiration, and signature.

### 2. Lifecycle & Consistency
Located in `com.forumhub.modules.topic.*`.
- **Status Integrity:** Validates state transitions.
- **Duplicate Prevention:** Blocks identical topics.
- **Dynamic Filtering:** Validates complex query filters.

---

## 🖥️ Frontend (Vue.js & Vitest)

### 1. State Management (Store)
Located in `src/stores/__tests__/*`.
- **Consistency:** Validates pagination reset on filter application.
- **Security:** Ensures `localStorage` cleanup on logout.

### 2. Components & UI
Located in `src/views/__tests__/*`.
- **Visual Feedback:** Validates loading spinners and error messages.
- **Mocks:** Uses JSDOM to simulate browser behavior.

---

## 🚀 Unified Secure Execution

The `./automation/test.sh` script orchestrates suite execution in isolated containers.

### ⚠️ Critical Requirement: Environment Variables
Tests **depend** on the `.env` file in the project root to configure test database credentials and JWT secrets.
- The script uses Docker Compose's `--env-file` flag to securely inject these variables.
- **Never** run tests without first creating the `.env` file (use `.env.example` as a template).

### Final Report
The script consolidates Maven (Backend) and Vitest (Frontend) results, returning success only if **both** pass.