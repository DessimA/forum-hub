# FórumHub

[![Project Status: Active](https://img.shields.io/badge/Project%20Status-Active-brightgreen.svg)]()
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Backend Coverage](https://img.shields.io/badge/Backend%20Coverage-80%2B%25-brightgreen.svg)]()
[![Frontend Coverage](https://img.shields.io/badge/Frontend%20Coverage-75%2B%25-brightgreen.svg)]()

## 📋 Descrição / Description

**PT-BR:** O FórumHub é uma plataforma moderna para troca de conhecimento, focada em estabilidade, segurança e auditoria. Desenvolvido com uma arquitetura de **Monolito Modular**, o projeto utiliza **UUIDs imutáveis** para garantir a rastreabilidade total e um sistema de recuperação de dados robusto. A interface foi refinada para um estilo **Clean Dark** profissional e intuitivo.

**EN:** FórumHub is a modern knowledge-sharing platform focused on stability, security, and auditing. Built with a **Modular Monolith** architecture, the project utilizes **immutable UUIDs** to ensure full traceability and a robust data recovery system. The interface has been refined to a professional and intuitive **Clean Dark** style.

---

## 📚 Documentação / Documentation

### [🇧🇷 Português](./docs/pt-br/README.md)
1. [Introdução](./docs/pt-br/01-introducao.md)
2. [Arquitetura](./docs/pt-br/02-arquitetura.md) (ADR-004 UUIDs)
3. [Infraestrutura](./docs/pt-br/03-infraestrutura.md) (Docker & Flyway)
4. [API](./docs/pt-br/04-api.md) (Paginação & JWT)
5. [Frontend](./docs/pt-br/05-frontend.md) (Clean Dark UI)
6. [Testes](./docs/pt-br/06-testes.md) (TDD & Integração)
7. [Banco de Dados](./docs/pt-br/07-banco-dados.md) (ERD & Schema)
8. [Melhorias](./docs/pt-br/08-melhorias.md) (Roadmap)
9. [Análise UX](./docs/pt-br/09-analise-ux-backend.md) (Integração)

### [🇺🇸 English](./docs/en/README.md)
1. [Introduction](./docs/en/01-introduction.md)
2. [Architecture](./docs/en/02-architecture.md)
3. [Infrastructure](./docs/en/03-infrastructure.md)
4. [API](./docs/en/04-api.md)
5. [Frontend](./docs/en/05-frontend.md)
6. [Testing](./docs/en/06-testing.md)
7. [Database](./docs/en/07-database.md)
8. [Improvements](./docs/en/08-improvements.md)
9. [UX Analysis](./docs/en/09-integration-analysis.md)

---

## 🚀 Quick Start

### Pré-requisitos / Prerequisites
- Docker & Docker Compose
- Node.js 22+ (Optional / Local Dev)

### Como executar / How to run (Automação / Automation)

**PT-BR:**
1. Clone o repositório.
2. Execute o script principal: `./automation/run.sh`
3. Siga o menu: selecione o Profile (Dev/Prod) e inicie o **Full Stack**.
4. Acesse: Backend em `localhost:8080` (Swagger) | Frontend em `localhost:3000`.

**EN:**
1. Clone the repository.
2. Run the main script: `./automation/run.sh`
3. Follow the menu: select the Profile (Dev/Prod) and start **Full Stack**.
4. Access: Backend at `localhost:8080` (Swagger) | Frontend at `localhost:3000`.

---

## 🛠️ Tecnologias / Tech Stack

- **Backend:** Java 17, Spring Boot 3.4.1, Hibernate, Flyway (MySQL 8), Spring Security (JWT).
- **Frontend:** Vue.js 3.5+ (Composition API), TypeScript, Pinia 2.3, Vuetify 3.11 (Subtle Neon UI), Vite 6.
- **Tests:** JUnit 5, Mockito, Vitest 4, Vue Test Utils.
- **Infra:** Docker Multi-profile, MySQL 8.4, Spring Actuator.