# 07 - Banco de Dados

Este documento descreve o esquema exato do banco de dados (MySQL 8) utilizado pelo FórumHub, conforme definido nas migrações Flyway.

## 🗄️ Modelo Entidade-Relacionamento (ERD)

O diagrama abaixo reflete a estrutura real das tabelas, incluindo tipos de dados e relacionamentos.

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
        text content "Backup de dados excluídos"
        text justification
        varchar(255) performed_by
        timestamp created_at
    }
```

## 📜 Estrutura e Migrações (Flyway)

O banco é 100% gerenciado via Flyway. O esquema evoluiu através das seguintes versões:

### Histórico de Versões
1. **V1:** Estrutura base (`users`, `profiles`, `courses`, `topics`, `answers`, `audit_logs`).
2. **V2:** Carga inicial de dados (Perfis e Usuário Admin).
3. **V3:** Sistema de bloqueio de usuários (`is_permanent_blocked`, `blocked_until`).
4. **V4:** Auditoria e justificação de suspensão.
5. **V5:** Índices de performance (`idx_topics_author`, `idx_answers_topic`, etc).

## 🔍 Tipagem e IDs
- **UUIDs:** Todos os IDs primários são `BINARY(16)` (UUID v4) para segurança e distribuição, não inteiros sequenciais.
- **Auditoria:** A tabela `audit_logs` armazena snapshots de entidades excluídas para fins de compliance e recuperação.