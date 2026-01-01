# 02 - Software Architecture

This document details the architectural decisions, system design, and patterns adopted in **ForumHub**. The application follows **Clean Code** and **SOLID** principles and uses a **Modular Monolith** architecture to ensure organization and scalability without premature microservices complexity.

## 🏗️ Overview (C4 Model)

ForumHub clearly separates the Frontend (SPA) from the Backend (REST API), communicating exclusively via JSON.

### Container Diagram (C4 Level 2)

```mermaid
graph TD
    User("👤 User<br/>(Browser/Mobile)")
    
    subgraph "Docker Host"
        Nginx("🌐 Nginx Proxy<br/>(Port 80/443)")
        
        subgraph "Frontend Container"
            VueApp("🖥️ Vue.js SPA<br/>(Vite Optimized)")
        end
        
        subgraph "Backend Container"
            SpringBoot("⚙️ Spring Boot API<br/>(Port 8080)")
        end
        
        subgraph "Data Container"
            MySQL("🗄️ MySQL 8.4<br/>(Port 3306)")
        end
    end

    User -->|HTTPS Request| Nginx
    Nginx -->|Serve Static| VueApp
    Nginx -->|Proxy /api| SpringBoot
    SpringBoot -->|JPA/Hibernate| MySQL
```

### Internal Backend Architecture (Modular Monolith)

The backend is divided into independent domain modules, reducing coupling and facilitating maintenance.

```mermaid
graph TD
    subgraph "API Layer (Controllers)"
        AuthController
        TopicController
        AnswerController
        CourseController
        UserController
        AuditController
    end

    subgraph "Core Modules (Service/Domain)"
        Auth[Auth Module]
        Topic[Topic Module]
        Answer[Answer Module]
        Course[Course Module]
        User[User Module]
        Audit[Audit Module]
    end

    subgraph "Infrastructure"
        Security["Spring Security / JWT"]
        Docs["SpringDoc OpenAPI"]
        I18n["MessageSource (EN/PT-BR)"]
        DB["MySQL Database"]
    end

    AuthController --> Auth
    TopicController --> Topic
    AnswerController --> Answer
    CourseController --> Course
    UserController --> User
    AuditController --> Audit

    Auth --> User
    Topic --> Course
    Topic --> User
    Answer --> Topic
    Answer --> User
    
    Auth --> Security
    Topic --> Security
    Answer --> Security
    User --> Security
    
    Auth --> DB
    Topic --> DB
    Answer --> DB
    Course --> DB
    User --> DB
    Audit --> DB

    Auth --> I18n
    Topic --> I18n
    
    AuthController --> Docs
    TopicController --> Docs
```

---

## 🏛️ Architectural Decisions (ADRs)

### 1. Frontend-Backend Separation (Decoupled Architecture)
- **Decision:** Separate UI (Vue.js) from Business Logic (Spring Boot).
- **Status:** Implemented.
- **Motivation:** Allows independent development and facilitates horizontal scaling of the backend.

### 2. Stateless Authentication (JWT)
- **Decision:** Use JSON Web Tokens (JWT) for authentication.
- **Status:** Implemented and Documented with Swagger.
- **Detail:** Bearer Auth support implemented in Swagger UI, allowing protected tests directly in the browser.

### 3. Database Migration (Flyway)
- **Decision:** Use Flyway for versioned database schema management.
- **Status:** Implemented (V1 to V5).

### 4. Deterministic UUIDs
- **Decision:** Use `BINARY(16)` for all primary IDs.
- **Motivation:** Superior performance over `VARCHAR(36)` and prevention against ID enumeration attacks.

### 5. Internationalization (i18n)
- **Decision:** Backend responds with error messages based on the `Accept-Language` header.
- **Status:** Implemented (`messages_en.properties` and `messages_pt_BR.properties` files).
- **Motivation:** Global API support, decoupling error messages from the source code.

---

## 🔌 Critical Data Flows

### 1. Secure Topic Creation
This flow demonstrates security validation and persistence.

```mermaid
sequenceDiagram
    participant Client as Frontend/Swagger
    participant Filter as SecurityFilter
    participant API as TopicController
    participant Service as TopicService
    participant DB as MySQL

    Client->>Filter: POST /topics (Header: Bearer Token)
    Filter->>Filter: Validate JWT Signature
    Filter->>Filter: Check User status (Active?)
    
    alt Invalid Token or Blocked User
        Filter-->>Client: 403 Forbidden
    else Valid Token
        Filter->>API: Forward Request (User Context)
        API->>Service: create(TopicRequestDTO)
        Service->>DB: Repository.save(Topic)
        DB-->>Service: Topic Saved (UUID)
        Service-->>API: TopicResponseDTO
        API-->>Client: 201 Created (JSON)
    end
```

---

## 📦 Design Patterns

### Backend
- **Controller-Service-Repository:** Well-defined classic layers.
- **DTO (Data Transfer Object):** 
  - Exclusive exposure of immutable objects (`Java Records`).
  - Naming and Documentation **100% in English** (e.g., `TopicRequestDTO`, `RegisterRequestDTO`).
  - Rich Swagger annotations (`@Schema`).
- **Global Exception Handling:** Centralized errors with `GlobalExceptionHandler`, returning standardized and translated responses.

### Frontend
- **Manual Chunking (Vite):** Build optimization by separating `vendor-vue` and `vendor-vuetify` for faster loading.
- **Service Layer:** Complete Axios abstraction in `src/services/api.ts`.