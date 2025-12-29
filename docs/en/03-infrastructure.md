# Infrastructure and Automation Ecosystem

## Containerization with Docker
FórumHub is designed to be environment-agnostic ("run anywhere").

### Backend Dockerfile
Uses **Multi-stage Build** for extreme optimization:
1. **Stage 1 (Build):** Uses the Maven image to compile code and run tests.
2. **Stage 2 (Final):** Uses a lightweight JRE image (Eclipse Temurin) to run the generated `.jar`, reducing the image size from ~600MB to ~200MB.

### Database (MySQL 8.0)
Configured for persistence through volumes. Flyway migrations ensure the database schema is faithfully recreated in any new container.

## Automation System (Shell Scripts)
The `automation/run.sh` script is the core of the developer experience.

### Script Features:
- **Requirement Verification:** Checks if Docker and Docker Compose are installed before starting.
- **Profile Management:**
  - `dev`: Activates DevTools and detailed logs.
  - `test`: Starts ephemeral containers only for running the test suite.
  - `prod`: Strict security settings and Nginx for the frontend.
- **Wait Orchestration:** The script uses `healthchecks` to ensure MySQL is accepting connections before attempting to start the Backend, preventing initialization failures.

## Simulated CI/CD Workflow
Although the project runs locally, the folder structure and scripts are ready for integration with GitHub Actions or Jenkins:
1. `cleanup.ps1` cleans the workspace.
2. `test.sh` performs full validation.
3. `docker-compose.yml` handles deployment.