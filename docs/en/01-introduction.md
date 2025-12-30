# 01 - Introduction to ForumHub

## 📖 About the Project
**ForumHub** is a modern and responsive educational forum platform. Its goal is to allow students and instructors to interact through question and answer topics, organized by courses.

The project was conceived focusing on **Clean Code**, **SOLID**, and **Engineering Best Practices**, serving not just as a functional product but as a robust Full Stack architecture reference.

---

## 📂 Directory Structure
The project organization reflects the separation of concerns (Frontend vs Backend vs Infrastructure).

```text
forum-hub/
├── automation/             # Automation Scripts (Local CI/CD)
│   ├── run.sh              # Docker CLI Manager
│   └── test.sh             # Unified Test Runner
│
├── backend/                # REST API (Java/Spring Boot)
│   ├── src/main/java/com/forumhub/
│   │   ├── config/         # Configs (Security, Swagger)
│   │   ├── modules/        # Segregated Domains (User, Topic, Course)
│   │   │   └── [domain]/
│   │   │       ├── controller/  # REST Endpoints
│   │   │       ├── service/     # Business Rules
│   │   │       ├── repository/  # Data Access
│   │   │       └── dto/         # API Contracts
│   │   └── security/       # Auth Logic (JWT)
│   └── src/main/resources/
│       └── db/migration/   # Versioned SQL Scripts (Flyway)
│
├── docs/                   # Project Documentation
│   ├── pt-br/              # Portuguese Documentation
│   └── en/                 # English Documentation
│
├── frontend/               # Single Page Application (Vue.js)
│   ├── src/
│   │   ├── components/     # Reusable Components (UI)
│   │   ├── views/          # Application Pages
│   │   ├── stores/         # Global State (Pinia)
│   │   ├── services/       # API Integration (Axios)
│   │   └── types/          # TypeScript Definitions
│   └── vite.config.ts      # Build Config
│
└── infra/                  # Infrastructure as Code
    ├── backend/            # Backend Dockerfile
    ├── frontend/           # Dockerfile and Nginx config
    ├── mysql/              # DB Init Scripts
    └── docker-compose.yml  # Service Orchestration
```

---

## 🚀 Key Features

| Module | Feature | Description |
| :--- | :--- | :--- |
| **Authentication** | Login/Register | Secure access via JWT with auto-expiration. |
| **Topics** | Full CRUD | Creation, paginated listing, editing, and deletion of questions. |
| **Answers** | Interaction | Ability to answer topics and mark solution. |
| **Dashboard** | Admin | User management and audit logs (Role-based). |

---

## 🏁 Getting Started

To run the project locally, refer to the infrastructure guide or use the automation script:

```bash
./automation/run.sh
```