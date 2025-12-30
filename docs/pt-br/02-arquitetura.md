# 02 - Arquitetura de Software

Este documento detalha as decisões arquiteturais, o design do sistema e os padrões adotados no **FórumHub**.

## 🏗️ Visão Geral (C4 Model)

O FórumHub segue uma arquitetura de **Monólito Modular** (Modular Monolith), separando claramente o Frontend (SPA) do Backend (API REST), mas mantendo a simplicidade de implantação.

### Diagrama de Container

```mermaid
graph TD
    User("👤 Usuário<br/>(Aluno ou Instrutor)")

    subgraph ForumHub [FórumHub System]
        direction TB
        SPA("🖥️ Single Page Application<br/>(Vue.js 3 + Vite)")
        API("⚙️ API Application<br/>(Java 17 + Spring Boot 3)")
        DB[("🗄️ Database<br/>(MySQL 8)")]
    end

    User -->|Usa HTTPS| SPA
    SPA -->|JSON/HTTPS| API
    API -->|JDBC| DB
```

---

## 🏛️ Decisões Arquiteturais (ADRs)

### 1. Separação Frontend-Backend (Decoupled Architecture)
- **Decisão:** Separar a UI (Vue.js) da Lógica de Negócio (Spring Boot).
- **Motivação:** Permite o desenvolvimento independente das equipes, facilita a escalabilidade horizontal do backend e possibilita a criação futura de outros clientes (ex: App Mobile) consumindo a mesma API.

### 2. Autenticação Stateless (JWT)
- **Decisão:** Uso de JSON Web Tokens (JWT) para autenticação.
- **Motivação:** Elimina a necessidade de sincronização de sessão no servidor (stateless), o que é ideal para ambientes conteinerizados e escaláveis.

### 3. Migração de Banco de Dados Versionada (Flyway)
- **Decisão:** Uso do Flyway para gerenciar o esquema do banco.
- **Motivação:** Garante que o estado do banco de dados seja reprodutível em qualquer ambiente (Dev, Test, Prod) e evita conflitos de alterações manuais SQL.

---

## 🔌 Fluxo de Dados Real

O diagrama abaixo ilustra o fluxo exato de uma requisição de criação de tópico, conforme implementado no `TopicService`:

```mermaid
sequenceDiagram
    participant User
    participant Filter as SecurityFilter
    participant API as TopicController
    participant Service as TopicService
    participant Validator as ValidationService
    participant DB as MySQL

    User->>Filter: POST /topics (Bearer Token)
    Filter->>Filter: Valida Token & Seta User Context
    Filter->>API: Repassa Requisição

    activate API
    API->>API: @Valid (Bean Validation)
    API->>Service: create(dto)
    
    activate Service
    Service->>Service: SecurityService.getCurrentUser()
    Service->>Validator: validateNotDuplicate(title, msg)
    Validator->>DB: count(title, msg)
    DB-->>Validator: 0
    
    Service->>DB: CourseRepository.findById(courseId)
    DB-->>Service: Course Entity
    
    Service->>Service: Mapper.toEntity(dto)
    Service->>DB: TopicRepository.save(topic)
    DB-->>Service: Topic Saved (UUID)
    
    Service-->>API: TopicResponseDTO
    deactivate Service
    
    API-->>User: 201 Created (Location Header)
    deactivate API
```

---

## 📦 Padrões de Projeto (Design Patterns)

### Backend
- **Controller-Service-Repository:** Separação clássica de responsabilidades.
  - *Controller:* Trata HTTP e validação de entrada.
  - *Service:* Contém as regras de negócio puras.
  - *Repository:* Abstração de acesso a dados (Spring Data JPA).
- **DTO (Data Transfer Object):** Nunca expomos as Entidades JPA diretamente na API. Usamos DTOs (`TopicoRequestDTO`, `TopicoResponseDTO`) para desacoplar o modelo de domínio do contrato da API.
- **Mapper Pattern:** Uso do **MapStruct** para converter Entidades <-> DTOs de forma performática e sem boilerplate.

### Frontend
- **Composition API:** Utilizada em vez da Options API para melhor reutilização de lógica (Composables) e inferência de tipos com TypeScript.
- **Store Pattern (Pinia):** Gerenciamento de estado centralizado para dados globais (Sessão de Usuário, Tema).
- **Service Layer:** O frontend possui uma camada de serviço (`src/services/api.ts`) que abstrai as chamadas HTTP (Axios), evitando acoplamento direto nos componentes visuais.