# 02 - Software Architecture: The Internal Mechanism

ForumHub uses a **Modular Monolith** architecture. This means that while the code is a single deployment unit, domains are logically separated to avoid coupling.

## 🧱 Modular Decomposition

```mermaid
graph LR
    subgraph "Security Module"
        Security[Spring Security Config]
        Token[JWT Token Service]
        Validator[SecurityValidationService]
    end

    subgraph "Domain Modules"
        User[User Module]
        Topic[Topic Module]
        Answer[Answer Module]
        Favorite[Favorite Module]
        Notification[Notification Module]
    end

    subgraph "Cross-Cutting Module"
        Audit[Audit & Restore Module]
        Common[Global Exception Handler]
    end

    User --- Topic
    Topic --- Answer
    Topic --- Favorite
    Topic --- Notification
    
    Validator --> User
    Validator -.-> Topic
    Validator -.-> Answer
    
    Topic --> Audit
    Answer --> Audit
```

---

## 🔐 Authentication and Blocking Validation Flow

Unlike common JWT systems, ForumHub validates the user's state in the database **on every write request**, even if the token is valid.

```mermaid
sequenceDiagram
    participant User as Client (Frontend)
    participant Filter as SecurityFilter
    participant JWT as TokenService
    participant DB as MySQL (Users)
    participant Controller as Business Controller

    User->>Filter: Request + Bearer Token
    Filter->>JWT: validateToken(token)
    JWT-->>Filter: Valid (User UUID)
    
    Filter->>DB: findUserById(uuid)
    DB-->>Filter: User Entity (status: ACTIVE)
    
    Filter->>Filter: Inject User in SecurityContext
    
    Filter->>Controller: Process Business Logic
    
    Note over Filter, DB: If user is blocked in DB:
    Filter->>DB: checkStatus()
    DB-->>Filter: BLOCKED
    Filter-->>User: 403 Forbidden (Reason: Account Suspended)
```

---

## 🚀 Processing Layers (Layered Architecture)

Each module strictly follows these layers:

1.  **Web Layer (Controller):** Defines endpoints, receives DTOs, and validates basic syntax via `@Valid`.
2.  **Business Layer (Service):** Where the "heart" of the system resides. Orchestrates transactions (`@Transactional`) and validates business rules (e.g., "Cannot reply to a closed topic").
3.  **Persistence Layer (Repository):** Spring Data JPA abstraction over SQL.
4.  **Security Validation Layer:** Interceptor filter that ensures the action performer is the resource owner or a moderator.

---

## 📡 Asynchronous Notification Flow (Internal Implementation)

When an event occurs (e.g., new reply), the system triggers notifications for all interested parties (whoever favorited the topic).

```mermaid
graph TD
    Trigger[New Reply Created] --> GetFavs[Find users who<br/>favorited the topic]
    GetFavs --> Loop{For each user}
    Loop --> CreateNotif[Create Notification in DB]
    CreateNotif --> Poll[Frontend detects via Polling]
```

## 🛠️ Infrastructure Fundamentals
- **CORS Config:** The backend allows requests only from the origin defined in `.env` (e.g., `localhost:3000`), blocking malicious sites from accessing the API.
- **BCrypt:** Passwords are saved with `cost factor 10`. Even if the database is leaked, passwords will take decades to crack by brute force.
- **Connection Pooling:** We use **HikariCP** configured to manage the MySQL connection pool, ensuring the application handles traffic spikes without crashing the database.
