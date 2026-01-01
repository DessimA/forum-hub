# 03 - Infrastructure and Docker

FórumHub adopts a simplified **"Infrastructure as Code"** philosophy, where all the environment necessary to run the project (database, backend, frontend) is defined and orchestrated via Docker.

## 🐳 Containerization and Orchestration

We use `docker-compose` to manage the application lifecycle.

### Core Services

| Service | Image | Exposed Port | Role | Critical Variables |
| :--- | :--- | :--- | :--- | :--- |
| **MySQL** | `mysql:8.4` | 3306 | Data Persistence | `MYSQL_ROOT_PASSWORD` |
| **Redis** | `redis:alpine` | 6379 | L2 Cache & Rate Limit | - |
| **Backend** | `eclipse-temurin:17` | 8080 | Spring Boot API | `SPRING_DATA_REDIS_HOST`, `API_SECURITY_TOKEN_SECRET` |
| **Frontend** | `nginx:stable-alpine` | 80 / 3000 | Web Server / Proxy | - |

---

## ⚡ Performance and Resilience (Redis)

FórumHub uses **Redis** for two critical functions:
1. **L2 Cache:** Storing paginated list results to avoid repetitive MySQL access.
2. **Rate Limiting:** Request flow control per IP.

### Network Configuration
All containers operate on the internal `forumhub-network`. The Backend communicates with Redis via the `redis` hostname, ensuring isolation and ease of configuration via Docker's internal DNS.

### Automatic Resilience
The application was developed with **Fail-Safe** for the cache. If the Redis service goes offline, the Backend catches the exception, logs a warning (`WARN`), and redirects the request directly to the database, ensuring the system remains functional.

---

## 🔐 Secrets and Configuration Management

**Security First:** No passwords or secrets are versioned in Git. The project uses a `.env` file in the root to inject sensitive settings into containers.

### How to Configure
1. Copy the example file:
   ```bash
   cp .env.example .env
   ```
2. Edit the `.env` with your credentials (or use default values for local development):
   ```properties
   MYSQL_ROOT_PASSWORD=root
   API_SECURITY_TOKEN_SECRET=your_jwt_secret_here
   ```

> **Note:** Automation scripts (`run.sh` and `test.sh`) are configured to fail if the `.env` file does not exist or variables are not loaded, preventing insecure execution.

---

## ⚙️ Automation and Scripts (CLI)

To facilitate day-to-day operations, we created a robust Bash CLI.

### 1. Main Manager (`automation/run.sh`)
Interactive interface to build and run the project.
- **Usage:** `./automation/run.sh`
- **Features:**
  - Profile Selection (Dev, Test, Prod).
  - Clean Build (clears volumes and rebuilds).
  - Quick Start (just starts containers).
  - Automatic `.env` loading.

### 2. Test Runner (`automation/test.sh`)
Runs the entire test suite in ephemeral containers, ensuring the test environment is identical to production.
- **Backend:** Executes `mvn test` in an isolated container.
- **Frontend:** Executes `npm run test:unit` in a Node.js container.
- **Integration:** Uses the `--env-file` flag to ensure tests have access to correct settings.

---

## 🚀 Environment Profiles (Spring Profiles)

The Backend supports multiple configuration profiles, controlled via `SPRING_PROFILES_ACTIVE`:

| Profile | Config File | Swagger Behavior | SQL Logs | Usage |
| :--- | :--- | :--- | :--- | :--- |
| **dev** | `application-dev.yml` | **Enabled** | Active | Local Development |
| **prod** | `application-prod.yml` | **Disabled** | Inactive | Production / Deploy |
| **test** | `application-test.yml` | Disabled | Minimal | Automated Testing |

---

## 🌐 Optimized Build (Frontend)

The Frontend uses **Vite** with a **Manual Chunking** strategy configured in `vite.config.ts`.
- **Result:** Separation of `vue` and `vuetify` libraries into distinct `.js` files (`vendor-vue`, `vendor-vuetify`).
- **Benefit:** Better browser caching and faster initial loading.