# Deep Architecture - FórumHub

## Design Patterns and Design Decisions

### 1. Layered Architecture
The backend follows the classic layered pattern but with modern refinements:
- **DTOs (Data Transfer Objects):** No JPA entities are directly exposed in the API. This prevents database logic leakage to the client and allows independent evolution of both sides.
- **Mappers (MapStruct):** We use MapStruct for automatic and performant conversions between Entities and DTOs, reducing boilerplate code.
- **Global Exception Handling:** A `@ControllerAdvice` centralizes error handling, ensuring the client always receives standardized responses even in case of unexpected failures.

### 2. Security Model (RBAC)
Security is based on functions (Role-Based Access Control):
- **JWT Token:** Contains custom claims (such as user email and permissions).
- **Stateless:** The server does not store sessions, which facilitates horizontal scalability.
- **Security Filter:** A filter intercepts each request, validates the token, and populates the Spring `SecurityContext`.

## Data Model (ER)
The database has been normalized to ensure data integrity and performance in complex queries.

```mermaid
erDiagram
    USER ||--o{ TOPIC : author
    USER ||--o{ ANSWER : author
    USER ||--o{ AUDIT_LOG : "performed by"
    PROFILE ||--o{ USER : has
    COURSE ||--o{ TOPIC : belongs
    TOPIC ||--o{ ANSWER : contains

    USER {
        bigint id
        string name
        string email
        string password
        bigint profile_id
    }
    TOPIC {
        bigint id
        string title
        string message
        enum status
        bigint author_id
        bigint course_id
    }
    ANSWER {
        bigint id
        string message
        boolean solution
        bigint topic_id
        bigint author_id
    }
    AUDIT_LOG {
        bigint id
        string action
        string entity_type
        bigint entity_id
        string justification
        datetime performed_at
    }
```

## Detailed Authentication Flow
The login and authorization process follows rigorous security standards:

```mermaid
sequenceDiagram
    participant U as User
    participant F as Vue.js (Frontend)
    participant S as Spring Security (Backend)
    participant J as JWT Service
    participant DB as MySQL

    U->>F: Enters credentials
    F->>S: POST /api/v1/auth/login
    S->>DB: Search user by email
    DB-->>S: Return BCrypt hash
    S->>S: Validate password (BCrypt match)
    alt Success
        S->>J: Request Token creation
        J-->>S: Return JWT (Signed with HS256)
        S-->>F: Return 200 OK + JWT
        F->>F: Store JWT in LocalStorage
    else Failure
        S-->>F: Return 401 Unauthorized
    end
```

## Testing Strategy
- **Unit Tests:** Focus on the business logic of Services using Mockito to isolate dependencies.
- **Integration Tests:** Use the `test` profile with H2 database to validate the full cycle Request -> Controller -> Service -> Repository.
- **Frontend:** Vitest ensures that Pinia states and mutations occur as expected.