# 02 - Software Architecture

This document details the architectural decisions, system design, and patterns adopted in **FórumHub**. The application follows **Clean Code**, **SOLID** principles and uses a **Modular Monolith** architecture to ensure organization and scalability without the premature complexity of microservices.

## 🏗️ Overview (C4 Model)

FórumHub clearly separates the Frontend (SPA) from the Backend (REST API), communicating exclusively via JSON.

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

### Backend Internal Architecture (Modular Monolith)

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
        FavoriteController
        NotificationController
    end

    subgraph "Core Modules (Service/Domain)"
        Auth[Auth Module]
        Topic[Topic Module]
        Answer[Answer Module]
        Course[Course Module]
        User[User Module]
        Audit[Audit Module]
        Favorite[Favorite Module]
        Notification[Notification Module]
    end

    subgraph "Infrastructure"
        Security["Spring Security / JWT"]
        Docs["SpringDoc OpenAPI"]
        I18n["MessageSource (EN/PT-BR)"]
        DB["MySQL Database"]
        Redis["Redis (Cache/Limit)"]
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
- **Decision:** Separate the UI (Vue.js) from the Business Logic (Spring Boot).
- **Status:** Implemented.
- **Motivation:** Allows independent team development and facilitates horizontal scaling of the backend.

### 2. Stateless Authentication (JWT)
- **Decision:** Use JSON Web Tokens (JWT) for authentication.
- **Status:** Implemented and Documented with Swagger.
- **Detail:** Implemented **Bearer Auth** support in Swagger UI, allowing protected tests directly in the browser.

### 3. Database Migration (Flyway)
- **Decision:** Use Flyway to manage the database schema in a versioned manner.
- **Status:** Implemented (V1 to V6).

### 4. Use of Deterministic UUIDs
- **Decision:** Use `BINARY(16)` for all primary IDs.
- **Motivation:** Superior performance compared to `VARCHAR(36)` and prevention against ID enumeration attacks.

### 5. Internationalization (i18n)
- **Decision:** Backend responds error messages based on the `Accept-Language` header.
- **Status:** Implemented (Files `messages_en.properties` and `messages_pt_BR.properties`).
- **Motivation:** Global support for the API, decoupling error messages from the source code.

---

## 🎨 Design System and UI/UX

The application adopts a **Deep Dark** aesthetic, focusing on readability and reducing eye fatigue, using a rigorous Design System.

### Visual Identity
- **Background:** Pure Black (#000000).
- **Accents:** Sky Blue (#38BDF8).
- **Surface:** Dark Grey (#111111).
- **Border Radius:** Standard 4px for buttons and cards.

### Responsive Strategy (Mobile-First)
- **Adaptive Navigation:** Desktop uses a fixed bar with links; Mobile uses a hamburger menu with a `v-navigation-drawer`.
- **Card Layout:** A dynamic grid that switches from 3 columns (Desktop) to 1 column (Mobile), ensuring an adequate touch area (minimum 36px height for fields).

---

## 🔌 Critical Data Flows

### 1. Notification System
The system uses a **Smart Polling** strategy on the frontend to keep the user updated without overloading the server.

```mermaid
sequenceDiagram
    participant User as Frontend (Pinia)
    participant API as NotificationController
    participant DB as MySQL

    rect rgb(20, 20, 20)
    Note right of User: Polling every 30s
    User->>API: GET /unread-count
    API->>DB: countByUserIdAndIsReadFalse()
    DB-->>API: Total: 5
    API-->>User: { count: 5 }
    User->>User: Updates Badge on Navbar
    end
```

### 2. Auditing and Restoration
Flow for protection against accidental or malicious deletion.

```mermaid
sequenceDiagram
    participant Admin
    participant Service as TopicService
    participant Audit as AuditLogService
    participant DB as MySQL

    Admin->>Service: DELETE /topics/{id}
    Service->>Audit: logDeletion(TopicData)
    Audit->>DB: INSERT INTO audit_logs (snapshot_json)
    Service->>DB: Physical DELETE (or Logical)
    
    Note over Admin, DB: Later...
    
    Admin->>Audit: POST /admin/logs/{id}/restore
    Audit->>DB: SELECT snapshot_json
    Audit->>Service: reconstruct(TopicData)
    Service->>DB: INSERT (with original UUID)
```

---

## ⚡ Security and Performance (Infrastructure)

### 1. Distributed Caching Layer (Redis)
- **Decision:** Use Redis as a Second Level Cache (L2 Cache).
- **Motivation:** Reduce latency and MySQL database resource consumption for frequent read operations (listings).
- **Implementation:** Cache applied to course and topic listings, with automatic invalidation (`@CacheEvict`) on write operations (POST/PUT/DELETE).

### 2. Traffic Control (Rate Limiting)
- **Decision:** Limit requests per IP using Redis.
- **Configuration:** Default limit of 100 requests per minute.
- **Motivation:** Prevent brute force attacks on login and mitigate DoS (Denial of Service) attempts.

### 3. Serialization Resilience (RestPage)
- **Decision:** Create the `RestPage<T>` wrapper for paginated objects.
- **Motivation:** Solve Jackson's limitation when deserializing Spring Data's `PageImpl` objects from Redis cache, eliminating the need for missing default constructors in the original class.

---

## 📦 Project Patterns (Design Patterns)

### Backend
- **Controller-Service-Repository:** Classic well-defined layers.
- **DTO (Data Transfer Object):** 
  - Exclusive exposure of immutable objects (`Java Records`).
  - **100% English** Naming and Documentation (e.g., `TopicRequestDTO`, `RegisterRequestDTO`).
  - Rich Swagger annotations (`@Schema`).
- **Global Exception Handling:** Centralization of errors with `GlobalExceptionHandler`, returning standardized and translated responses.

### Frontend
- **Manual Chunking (Vite):** Build optimization separating `vendor-vue` and `vendor-vuetify` for faster loading.
- **Service Layer:** Complete abstraction of Axios in `src/services/api.ts`.
