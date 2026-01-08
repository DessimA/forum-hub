# 04 - REST API: Contracts and Data Flows

The ForumHub API is the central point of communication. It was designed to be **consistent**, **predictable**, and **documented**.

## 🔑 Resource Creation Flow (Topics)

Creating a topic involves multiple validations and automatic triggers.

```mermaid
sequenceDiagram
    participant App as Frontend
    participant API as TopicController
    participant Val as Validation Layer
    participant Serv as TopicService
    participant DB as MySQL

    App->>API: POST /topics (JSON Payload)
    API->>Val: Syntactic Validation (@NotBlank, @Size)
    
    alt Invalid
        Val-->>App: 400 Bad Request (Error details)
    else Valid
        API->>Serv: create(DTO)
        Serv->>Serv: Validate if course exists
        Serv->>Serv: Validate title duplication
        
        Serv->>DB: INSERT INTO topics
        DB-->>Serv: OK (Entity + UUID)
        
        Serv->>Serv: Trigger Events (Notify course followers?)
        
        Serv-->>API: ResponseDTO
        API-->>App: 201 Created + Location Header
    end
```

---

## 📡 Smart Listing (Filters and Pagination)

The listing doesn't just return data; it applies dynamic filters and caching.

| Endpoint | Parameters | Description |
| :--- | :--- | :--- |
| `GET /api/v1/topics` | `page`, `size`, `sort`, `title`, `status`, `courseId` | Lists topics with stable pagination. |

### Golden Rule of Pagination:
Pages start at index **0**. If the frontend requests page 1 with 10 items, it will receive items 11 through 20.

---

## 🔐 Role-Based Access Control (RBAC)

We implement granular control over who can do what:

```mermaid
graph TD
    subgraph "Access Profiles"
        USER[ROLE_USER]
        MOD[ROLE_MODERATOR]
        ADMIN[ROLE_ADMIN]
    end

    USER -->|Can| Create[Create Topics/Answers]
    USER -->|Can| EditOwn[Edit Own Content]
    
    MOD -->|Can| Lock[Close Topics]
    MOD -->|Can| Suspend[Suspend Users]
    
    ADMIN -->|Can| FullAccess[Total Access]
    ADMIN -->|Can| Restore[Restore Deleted Items]
    ADMIN -->|Can| Audit[View Audit Logs]
```

---

## 🛠️ Validation Details (Exact Fields)

For each creation request, the system validates:
- **Title:** Minimum 5, maximum 100 characters.
- **Message:** Minimum 10, maximum 5000 characters.
- **Course:** Must be an active course ID in the system.

### Validation Error Response Example (400):
```json
{
  "timestamp": "2026-01-07T18:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "errors": {
    "title": "Title cannot be blank",
    "message": "Message must be at least 10 characters"
  }
}
```

---

## 📚 Swagger Documentation (OpenAPI)
Available at: `http://localhost:8080/swagger-ui.html`
- Documentation is automatically generated from code annotations.
- Each endpoint has a JSON input example.
- Models (Schemas) describe each field, whether it's mandatory, and its format (e.g., UUID).