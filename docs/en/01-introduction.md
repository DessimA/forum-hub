# 01 - Introduction to ForumHub

## 📖 About the Project
**ForumHub** is a high-performance educational forum platform focused on proactive security and data integrity. The system enables seamless interaction between students and instructors, with total traceability of actions.

The project uses a **Modular Monolith** architecture, ensuring business logic is isolated and maintainable, while the interface delivers a modern and responsive user experience.

---

## 📂 Directory Structure
Official repository organization:

```text
forum-hub/
├── automation/             # Public execution and test scripts
│   ├── run.sh              # Docker CLI Manager (Interactive Menu)
│   └── test.sh             # Unified Test Runner (Maven + Vitest)
│
├── backend/                # Spring Boot 3.4.1 REST API
│   ├── src/main/java/com/forumhub/
│   │   ├── modules/        # Domains (User, Topic, Course, Answer, Audit)
│   │   ├── security/       # Defense in Depth and JWT
│   │   └── exception/      # Global error handling (Business & Integrity)
│   └── src/main/resources/db/migration # Flyway versioning
│
├── frontend/               # Vue.js 3.5 + Vuetify 3.11 SPA
│   ├── src/
│   │   ├── stores/         # Pinia (Search and Auth global state)
│   │   ├── views/          # Dialog-based interfaces
│   │   └── services/       # Axios API client
│   └── vite.config.ts      # Build and Vitest 4 configuration
│
└── infra/                  # Infrastructure (Docker & Nginx)
```

---

## 🚀 Key Features (Current State)

| Module | Feature | Description |
| :--- | :--- | :--- |
| **Advanced Search** | Filters & Sort | Search by title, course, status, and year with dynamic sorting. |
| **Lifecycle** | Lock/Unlock | Authors and Moderators can close and reopen discussions. |
| **Moderation** | Auto-Cleanup | User punishments automatically remove linked offensive content. |
| **Security** | Real-time Check | Account status verification on every API request. |
| **Auditing** | Full Restore | Topic and answer recovery preserving original UUIDs. |
| **Interface** | Modern UX | 100% Dialog and Snackbar based UI (no native popups). |
