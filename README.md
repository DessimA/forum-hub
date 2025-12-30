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
1. [Arquitetura](./docs/pt-br/02-arquitetura.md) (ADR-004 UUIDs)
2. [Infraestrutura](./docs/pt-br/03-infraestrutura.md) (Docker & Flyway)
3. [API](./docs/pt-br/04-api.md) (Paginação & JWT)
4. [Frontend](./docs/pt-br/05-frontend.md) (Clean Dark UI)
5. [Testes](./docs/pt-br/06-testes.md) (TDD & Integração)

### [🇺🇸 English](./docs/en/README.md)
1. [Architecture](./docs/en/02-architecture.md)
2. [Infrastructure](./docs/en/03-infrastructure.md)
3. [API](./docs/en/04-api.md)
4. [Frontend](./docs/en/05-frontend.md)
5. [Testing](./docs/en/06-testing.md)

---

## 🚀 Quick Start

### Pré-requisitos / Prerequisites
- Docker & Docker Compose
- Git

### Como executar / How to run (Scripts de Automação)

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

- **Backend:** Java 17, Spring Boot 3.4, Hibernate, Flyway (MySQL/H2 parity), Spring Security (JWT).
- **Frontend:** Vue.js 3 (Composition API), TypeScript, Pinia, Vuetify 3 (Subtle Neon UI), Vite.
- **Infra:** Docker Multi-profile, MySQL 8.4, Spring Actuator.
- **Tests:** JUnit 5, Mockito, Vitest, Vue Test Utils.
