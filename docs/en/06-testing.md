# 06 - Detailed Testing Strategy

ForumHub uses a robust testing pyramid to ensure business rule stability, data security, and UI integrity.

## 🏗️ Backend (Java & Spring Boot)

### 1. Security & Authentication Tests
Located in `com.forumhub.security.*`, these tests validate the application's defensive perimeter.
- **Active Session vs. Blocking:** Ensures that if an admin blocks a user, their access is revoked **immediately**, even if the JWT token is still valid (Real-time DB check).
- **Token Validation:** Integrity tests for JWT, expiration, and signature (HMAC256).
- **Role Hierarchy:** Validation that a `MODERATOR` can suspend a `USER` but is blocked from performing any action against an `ADMIN`.

### 2. Topic Lifecycle & Consistency
Located in `com.forumhub.modules.topic.*`.
- **Status Integrity:** If the only answer marked as **Solution** is deleted, the system must automatically revert the topic status from `RESOLVED` back to `OPEN`.
- **Duplicate Prevention:** Blocking topics with identical titles and messages to prevent spam and redundancy.
- **Dynamic Filtering:** Validation that filters for course, status, and text search return only the correct data subsets.

### 3. Contextual Moderation & Auditing
Located in `com.forumhub.modules.audit.*` and `com.forumhub.modules.user.*`.
- **Cascade Backup:** Validates that when deleting a topic for moderation, all associated answers also generate individual audit logs before being removed by DB cascade.
- **Faithful Restoration:** Ensures a restored item recovers its **original UUID**, maintaining the integrity of external links and cross-references.
- **Auto-Cleanup:** Tests if punishing a user while passing a message context automatically removes that message from the system.

---

## 🖥️ Frontend (Vue.js & Vitest)

### 1. State Management (Store)
Located in `src/stores/__tests__/*`.
- **Filter Consistency:** Validates that the Pinia Store clears search parameters and resets pagination to page zero when applying a new filter.
- **Logout Flow:** Ensures that `localStorage` and application state are completely destroyed upon logging out, preventing data leakage between users on the same browser.

### 2. Components & UI
Located in `src/views/__tests__/*` and `src/components/__tests__/*`.
- **Loading States:** Validation that the visual spinner appears exactly during the request time and disappears after the response.
- **Status Rendering:** Ensures colors and labels (`OPEN`, `CLOSED`, `RESOLVED`) faithfully match data from the API.
- **Browser Mocks:** Implementation of mocks for `ResizeObserver` and `IntersectionObserver` to ensure complex Vuetify components behave deterministically in the testing environment (JSDOM).

---

## 🚀 Unified Execution
The `./automation/test.sh` script orchestrates the execution of all suites in isolated containers, ensuring the testing environment is an exact replica of production.
- **Final Report:** The script consolidates Maven and Vitest exit codes to provide a unified verdict on the project's health.
