# 03 - Infrastructure and Docker

ForumHub adopts a simplified **"Infrastructure as Code"** philosophy, where the entire environment needed to run the project (database, backend, frontend) is defined and orchestrated via Docker.

## 🐳 Containerization and Orchestration

We use `docker-compose` to manage the application lifecycle.

### Core Services

| Service | Image | Exposed Port | Function | Critical Variables |
| :--- | :--- | :--- | :--- | :--- |
| **MySQL** | `mysql:8.4` | 3306 | Data persistence | `MYSQL_ROOT_PASSWORD` |
| **Backend** | `eclipse-temurin:17` | 8080 | Spring Boot API | `SPRING_DATASOURCE_PASSWORD`, `API_SECURITY_TOKEN_SECRET` |
| **Frontend** | `nginx:stable-alpine` | 80 / 3000 | Web Server / Proxy | - |

---

## 🔐 Secrets Management and Configuration

**Security First:** No passwords or secrets are versioned in Git. The project uses an `.env` file in the root to inject sensitive settings into containers.

### How to Configure
1. Copy the example file:
   ```bash
   cp .env.example .env
   ```
2. Edit the `.env` with your credentials (or use the default local development values):
   ```properties
   MYSQL_ROOT_PASSWORD=root
   API_SECURITY_TOKEN_SECRET=your_jwt_secret_here
   ```

> **Note:** Automation scripts (`run.sh` and `test.sh`) are configured to fail if the `.env` file does not exist or variables are not loaded, preventing insecure execution.

---

## ⚙️ Automation and Scripts (CLI)

To facilitate daily tasks, we created a robust CLI in Bash.

### 1. Main Manager (`automation/run.sh`)
Interactive interface to build and run the project.
- **Usage:** `./automation/run.sh`
- **Features:**
  - Profile selection (Dev, Test, Prod).
  - Clean Build (clears volumes and rebuilds).
  - Quick Start (just starts the containers).
  - Automatic `.env` loading.

### 2. Test Runner (`automation/test.sh`)
Runs the entire test suite in ephemeral containers, ensuring the test environment is identical to production.
- **Backend:** Runs `mvn test` in an isolated container.
- **Frontend:** Runs `npm run test:unit` in a Node.js container.
- **Integration:** Uses the `--env-file` flag to ensure tests have access to the correct settings.

---

## 🚀 Environment Profiles (Spring Profiles)

The Backend supports multiple configuration profiles, controlled via `SPRING_PROFILES_ACTIVE`:

| Profile | Config File | Swagger Behavior | SQL Logs | Use |
| :--- | :--- | :--- | :--- | :--- |
| **dev** | `application-dev.yml` | **Enabled** | Active | Local Development |
| **prod** | `application-prod.yml` | **Disabled** | Inactive | Production / Deploy |
| **test** | `application-test.yml` | Disabled | Minimal | Automated Testing |

### Production Security
In the `prod` profile:
- Swagger (`/swagger-ui.html`) returns 404/403.
- `Actuator` health details are hidden.
- Strict database schema validation (`validate`).

---

## 🌐 Optimized Build (Frontend)

The Frontend uses **Vite** with a **Manual Chunking** strategy configured in `vite.config.ts`.
- **Result:** Separation of `vue` and `vuetify` libraries into distinct `.js` files (`vendor-vue`, `vendor-vuetify`).
- **Benefit:** Better browser caching and faster initial load time.
