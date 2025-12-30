# 04 - API Documentation

## 🔐 Security and Authentication
The API utilizes **Stateless Authentication** with **JWT** Tokens.
- All routes under `/api/v1` (except `/auth/**` and `GET /courses`) require the `Authorization: Bearer <token>` header.
- **Standardization:** All input and output fields use **English** names (`name`, `password`, `email`, `id`).

## 📍 Featured Endpoints

### Authentication (`/api/v1/auth`)
- `POST /login`: Authenticates and returns the JWT. Payload: `{ "email", "password" }`.
- `POST /register`: Creates a new account with the `ROLE_USER` profile.

### Topics (`/api/v1/topics`)
- `GET /`: Paginated listing (`Page<TopicResponseDTO>`). Supported parameters: `page`, `size`, `sort`.
- `POST /`: Topic creation. The author is automatically extracted from the security context.
- `DELETE /{id}`: Logical deletion with audit log entry. Requires justification if performed by a moderator.

### Courses (`/api/v1/courses`)
- `GET /`: Now with **Mandatory Pagination** to ensure scalability.
- `POST /`: Course registration (Restricted access: `ROLE_ADMIN`).

### Users and Administration (`/api/v1/users`)
- `GET /profile`: Logged-in user data.
- `POST /{id}/block`: Support for **Permanent** or **Temporary** (days) block.
- `POST /admin/logs/{id}/restore`: Unique system for restoring deleted entities.

## 📖 Complete Documentation (Swagger)
The API is fully documented with **SpringDoc / OpenAPI 3**. Access it to see all data schemas and error codes:
`http://localhost:8080/swagger-ui.html`