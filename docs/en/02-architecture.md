# 02 - System Architecture

## 🏗️ Modular Monolith
The system utilizes a **Modular Monolith** architecture. This means that although everything is delivered in a single artifact, the code is organized into independent modules with well-defined responsibilities.

### Block Diagram
```mermaid
graph TD
    subgraph "Presentation Layer"
        UI[Vue.js SPA]
    end
    
    subgraph "API Layer (Spring Boot)"
        direction TB
        Auth[Auth Module]
        Topic[Topic Module]
        Answer[Answer Module]
        UserM[User Module]
        Audit[Audit Module]
    end
    
    subgraph "Persistence"
        DB[(MySQL)]
        Flyway[Flyway Migrations]
    end
    
    UI <--> Auth
    UI <--> Topic
    Topic --> Answer
    Topic --> UserM
    Answer --> UserM
    
    Auth --> DB
    Topic --> DB
    UserM --> DB
    Audit --> DB
```

## 📐 Design Principles
- **SOLID:** Each class has a single responsibility. For example, we created `SecurityValidationService` specifically to handle authorization rules, removing this burden from the controllers.
- **DRY (Don't Repeat Yourself):** Common validations were moved to utility services.
- **Clean Code:** Method names reveal their intention (e.g., `markAsSolution` instead of `updateStatus`).
