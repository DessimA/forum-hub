# Detailed Introduction - FórumHub

## The Challenge
FórumHub was born from a challenge proposed in the **Oracle Next Education (ONE)** program in partnership with **Alura**. The initial goal was to create a REST API to manage forum topics, but the project was expanded into a complete **Enterprise Full-Stack** application.

## Development Philosophy
This project focuses not only on functionality but on **technical excellence**. The following were strictly applied:
- **TDD (Test Driven Development):** Tests written before logical implementation.
- **SOLID:** Design principles to ensure sustainable and decoupled code.
- **Clean Code:** Semantic naming, small functions, and maximum readability.
- **DRY (Don't Repeat Yourself):** Abstraction of repetitive logic into services and utilities.

## Ecosystem Overview
The application is divided into two large blocks that communicate asynchronously and securely:

### 1. Backend (The Core)
Built with **Spring Boot 3**, the backend manages all persistence logic, business rules, and security. It uses a layered architecture:
- **Controller:** Request entry point, responsible for input validation (`@Valid`) and Swagger documentation.
- **Service:** Where the "magic" happens. Contains complex business rules and transaction orchestration.
- **Repository:** Persistence layer that uses Spring Data JPA for communication with MySQL/H2.
- **Security:** Custom filters for JWT token interception and authority management.

### 2. Frontend (The Experience)
A modern SPA (Single Page Application) with **Vue.js 3**, focused on performance and usability:
- **Composition API:** Organization of reusable logic through composables.
- **Pinia:** Centralized global state management (Auth, Topics, Users).
- **Vuetify 3:** Material Design-based interface with a custom "Neon Dark" theme.

## Advanced Use Cases
- **Topic Lifecycle:** A topic can transition from `OPEN` to `RESOLVED` when the author marks an answer as a solution.
- **Auditing System:** Each topic deletion generates an immutable trace in the database, recording the action's author and the justification provided.
- **Data Protection:** No passwords are stored in plain text; we use BCrypt with 12 rounds of hashing.