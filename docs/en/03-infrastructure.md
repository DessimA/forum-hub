# 03 - Infrastructure and Docker

## 🐳 Containerization
The project is 100% containerized to ensure it runs the same way on any machine. We use `docker-compose` to orchestrate three main services:

| Service | Base Image | Port | Description |
| :--- | :--- | :--- | :--- |
| **MySQL** | `mysql:8.4` | 3306 | Relational database. |
| **Backend** | `eclipse-temurin:17` | 8080 | Spring Boot API. |
| **Frontend** | `nginx:alpine` | 80/3000 | Vue.js application. |

## ⚙️ Execution Profiles
The system supports multiple profiles through Docker Compose:
- **prod:** Optimized for performance, WARN level logs.
- **dev:** Hot-reload enabled, DEBUG level logs.
- **test:** In-memory H2 database, automatic data cleanup.

## 🗄️ Database Evolution (Flyway)
Tables are not created manually. Flyway manages migrations:
- `V1`: Initial structure (tables and foreign keys).
- `V2`: Initial data (default Admin and courses).
- `V5`: Performance indexes for fast searches.
