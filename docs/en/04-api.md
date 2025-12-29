# 04 - API Documentation

## 🔐 Security
All routes (except login/register) require a JWT Token in the Header:
`Authorization: Bearer <your_token>`

## 📍 Key Endpoints

### Authentication
- `POST /api/v1/auth/login`: Returns the JWT token.
- `POST /api/v1/auth/register`: New user registration.

### Topics
- `GET /api/v1/topics`: Paginated list.
- `POST /api/v1/topics`: Creation (Requires ROLE_USER).
- `DELETE /api/v1/topics/{id}`: Deletion (Requires justification if not the author).

## 📖 Swagger (OpenAPI)
Full interactive documentation (where you can test the endpoints) is available at:
`http://localhost:8080/swagger-ui.html`
