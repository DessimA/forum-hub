# 07 - Database

This document describes the exact database schema (MySQL 8) used by ForumHub, as defined in the Flyway migrations.

## 🗄️ Entity-Relationship Diagram (ERD)

The diagram below reflects the actual table structure, including data types and relationships.

```mermaid
erDiagram
    PROFILES ||--o{ USERS : "define role"
    USERS ||--o{ TOPICS : creates
    USERS ||--o{ ANSWERS : writes
    COURSES ||--o{ TOPICS : categorizes
    TOPICS ||--o{ ANSWERS : contains

    PROFILES {
        binary(16) id PK
        varchar(50) name UK "ROLE_ADMIN, ROLE_USER..."
    }

    USERS {
        binary(16) id PK
        varchar(100) name
        varchar(255) email UK
        varchar(255) password
        binary(16) profile_id FK
        boolean is_permanent_blocked
        timestamp blocked_until
        text suspension_justification
        timestamp created_at
        timestamp updated_at
    }

    COURSES {
        binary(16) id PK
        varchar(100) name
        varchar(50) category
        timestamp created_at
    }

    TOPICS {
        binary(16) id PK
        varchar(255) title
        text message
        varchar(20) status "OPEN, CLOSED, SOLVED"
        binary(16) author_id FK
        binary(16) course_id FK
        timestamp created_at
        timestamp updated_at
    }

    ANSWERS {
        binary(16) id PK
        text message
        boolean solution
        binary(16) topic_id FK
        binary(16) author_id FK
        timestamp created_at
        timestamp updated_at
    }

    AUDIT_LOGS {
        binary(16) id PK
        varchar(50) action_type "DELETE, UPDATE..."
        varchar(50) entity_type "TOPIC, USER..."
        varchar(36) entity_id
        text content "Backup of deleted data"
        text justification
        varchar(255) performed_by
        timestamp created_at
    }
```

## 📜 Structure and Migrations (Flyway)

The database is 100% managed via Flyway. The schema has evolved through the following versions:

### Version History
1. **V1:** Base structure (`users`, `profiles`, `courses`, `topics`, `answers`, `audit_logs`).
2. **V2:** Initial data load (Profiles and Admin User).
3. **V3:** User blocking system (`is_permanent_blocked`, `blocked_until`).
4. **V4:** Audit and suspension justification.
5. **V5:** Performance indexes (`idx_topics_author`, `idx_answers_topic`, etc).

## 🔍 Types and IDs
- **UUIDs:** All primary IDs are `BINARY(16)` (UUID v4) for security and distribution, not sequential integers.
- **Audit:** The `audit_logs` table stores snapshots of deleted entities for compliance and recovery.