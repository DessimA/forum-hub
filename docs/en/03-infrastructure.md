# 03 - Infrastructure and Docker

## 🐳 Containerization
The project is 100% containerized to ensure parity between environments. We use `docker-compose` to orchestrate the main services:

| Service | Base Image | Port | Description |
| :--- | :--- | :--- | :--- |
| **MySQL** | `mysql:8.4` | 3306 | Data persistence (Volumes in `./infra/mysql_data`). |
| **Backend** | `eclipse-temurin:17` | 8080 | Spring Boot API (Multi-stage build). |
| **Frontend** | `nginx:stable-alpine` | 80/3000 | Vue.js SPA served by Nginx. |

## ⚙️ Automation Scripts
We facilitate execution through bash scripts:
- `./automation/run.sh`: Interactive menu to manage profiles and services.
- `./automation/test.sh`: Complete test suite (Backend + Frontend) via Docker.

### Execution Profiles
- **dev:** Development environment with MySQL database.
- **test:** Isolated environment for testing using H2 (MySQL Mode) and JUnit 5.
- **prod:** Optimized production configuration.

## 🗄️ Database Management (Flyway)
The system uses Flyway for schema version control. Migrations have been stabilized to be compatible with both **MySQL 8.4** and **H2 (in compatibility mode)**:
- **V1:** Fundamental tables with UUID (`BINARY(16)`).
- **V2:** Initial data using standard hexadecimal literals (`X'...'`) for cross-compatibility.
- **V3/V4:** Moderation and auditing extensions.
- **V5:** Performance indexes (`idx_topics_author`, `idx_audit_entity`, etc).

## 💓 Health and Monitoring
- **Actuator:** `http://localhost:8080/actuator/health` provides real-time status.
- **Swagger UI:** `http://localhost:8080/swagger-ui.html` for interactive endpoint testing.