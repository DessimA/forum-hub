# 10 - Improvement Roadmap & Technical Debt

This document lists improvement points identified within the current project scope, aiming to consolidate ForumHub's quality, security, and maintainability.

## 🚨 High Priority (Security & Stability)

### 1. Secrets Management
- **Issue:** Currently, secrets like `API_SECURITY_TOKEN_SECRET` and database passwords are hardcoded in `docker-compose.yml`.
- **Solution:** Implement environment variable reading (`.env`) which are not versioned.
- **Impact:** Prevents credential leakage in public repositories.

### 2. Pagination Serialization (Spring Data)
- **Issue:** Logs indicate a warning about unstable `PageImpl` serialization (`Serializing PageImpl instances as-is is not supported`).
- **Solution:** Configure `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` or migrate to Spring HATEOAS `PagedModel`.
- **Impact:** Ensures stable JSON structure for paginated lists consumed by the frontend.

## 🛠️ Medium Priority (Maintainability & DX)

### 3. API Documentation (Swagger/OpenAPI)
- **Issue:** DTOs (`TopicRequestDTO`, etc.) lack `@Schema` annotations.
- **Solution:** Add descriptions and examples to DTO fields.
- **Impact:** Improves Swagger UI usability for frontend developers and third parties.

### 4. Internationalization (i18n)
- **Issue:** Backend error messages (`GlobalExceptionHandler`) and Frontend texts are hardcoded in Portuguese.
- **Solution:**
  - **Backend:** Implement `MessageSource` with `.properties` files (messages_pt.properties, messages_en.properties).
  - **Frontend:** Implement `vue-i18n`.
- **Impact:** Allows the system to serve global users, aligning with the bilingual documentation.

## ⚡ Low Priority (Optimization)

### 5. Frontend Build Optimization
- **Issue:** Vite build warns about chunks larger than 500kB.
- **Solution:** Configure `rollupOptions` in `vite.config.ts` to split vendor chunks (separate `vuetify` and `vue` from the main bundle).
- **Impact:** Improves initial application load time.

### 6. Error Handling Standardization (RFC 7807)
- **Issue:** Current error format is custom.
- **Solution:** Adopt **Problem Details for HTTP APIs (RFC 7807)** standard.
- **Impact:** Increases interoperability with standard HTTP clients.
