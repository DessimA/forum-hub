# 04 - API Documentation (REST)

The ForumHub API is built on REST principles, using JSON as the message exchange format and JWT (JSON Web Tokens) for stateless security.

## 📚 Swagger UI (Interactive Documentation)

The easiest way to explore the API is through the Swagger UI, available in development environments.

- **URL:** `http://localhost:8080/swagger-ui.html`
- **Features:**
  - View all endpoints.
  - Detailed schemas for input and output data (DTOs).
  - **"Authorize" Button:** Allows injecting the JWT Token to test protected endpoints directly in the browser.

> **Security:** In production environments (`profile: prod`), Swagger is automatically disabled for data protection.

---

## 🔐 Authentication and Security

All protected endpoints require the header:
`Authorization: Bearer <your_jwt_token>`

### Auth Endpoints
- **POST** `/api/v1/auth/login`
  - **Input:** `LoginRequestDTO` (email, password)
  - **Output:** `TokenResponseDTO` (token)
  - **Errors:** 401 (Invalid Credentials)

- **POST** `/api/v1/auth/register`
  - **Input:** `RegisterRequestDTO` (name, email, password)
  - **Output:** `UserResponseDTO`
  - **Errors:** 409 (Email already registered)

---

## 🌍 Internationalization (i18n)

The API supports multiple languages for error messages. The client can request the desired language through the HTTP header:

- **Header:** `Accept-Language: pt-BR` (Default: English if not provided)
- **Error Response:**
  ```json
  {
    "timestamp": "2025-12-31T10:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access denied: you do not have permission...",
    "path": "/api/v1/users/123"
  }
  ```

---

## 📡 Key Endpoints

### Topics (`/api/v1/topics`)
Discussion management.
- **GET** `/`: Paginated listing. Filters: `courseId`, `status`, `year`, `title`.
  - *Stability:* Pagination serialization is guaranteed via DTO (`VIA_DTO`), ensuring stable contracts for the frontend.
- **POST** `/`: Creation (`TopicRequestDTO`).
- **GET** `/{id}`: Full details (`TopicDetailResponseDTO`).
- **PUT** `/{id}`: Update (`TopicUpdateDTO`).
- **DELETE** `/{id}`: Logical deletion.

### Answers (`/api/v1`)
- **POST** `/topics/{topicId}/answers`: Reply to a topic.
- **PATCH** `/answers/{id}/solution`: Mark answer as solution.

### Courses (`/api/v1/courses`)
- **GET** `/`: Public course listing.
- **POST/PUT/DELETE**: Administrative course management.

### Users (`/api/v1/users`)
- **GET** `/profile`: Logged-in user data.
- **POST** `/{id}/block`: Administrative/Moderative block (`BlockRequestDTO`).

---

## 📐 DTO Patterns (Data Transfer Objects)

To ensure consistency and professionalism, **all API fields and annotations are in English**.

| DTO | Description | Field Examples |
| :--- | :--- | :--- |
| `RegisterRequestDTO` | Registration data | `name`, `email`, `password` |
| `TopicRequestDTO` | New topic | `title`, `message`, `courseId` |
| `TopicDetailResponseDTO` | Topic read | `authorIsBlocked`, `answers` (List) |

This standardization facilitates integration with international clients and follows global Java development conventions.