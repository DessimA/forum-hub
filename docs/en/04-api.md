# API Technical Reference

The FórumHub API follows the OpenAPI 3.0 specification and uses the most modern REST design patterns.

## Design Principles
- **Plural Nouns:** `/topics`, `/users`, `/answers`.
- **URL Versioning:** `/api/v1/...`
- **Mandatory Pagination:** All listings use pagination via Spring Data to avoid network overload.

## Critical Endpoints Detail

### 1. Topic Management
- **DELETE `/api/v1/topics/{id}`**
  - **Permissions:** Topic author, Moderator, or Admin.
  - **Requirement:** Passing the `justification` parameter in the query string is mandatory. This justification is saved in the audit log.
  - **Effect:** Cascade deletion of all associated answers.

### 2. User Management (Moderation)
- **POST `/api/v1/users/{id}/block`**
  - **Moderator:** Can suspend users for a maximum of 24 hours.
  - **Admin:** Can apply permanent suspensions.
  - **Validation:** A user cannot block themselves.

### 3. Auditing System
- **GET `/api/v1/audit-logs`**
  - **Admin Only.**
  - **Content:** Returns a detailed list of who performed destructive or administrative actions in the system, allowing total traceability.

## Error Handling (Standard Responses)
All failures return the following JSON object:
```json
{
  "timestamp": "2025-12-29T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied: you do not have permission to perform this action.",
  "path": "/api/v1/users/5/block"
}
```

## Interactive Documentation
The Swagger UI (`/swagger-ui.html`) allows you to test JWT tokens directly in the browser through the "Authorize" option in the upper right corner.