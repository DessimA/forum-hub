# FórumHub

[![Project Status: Active](https://img.shields.io/badge/Project%20Status-Active-brightgreen.svg)]()
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Backend Coverage](https://img.shields.io/badge/Backend%20Coverage-80%2B%25-brightgreen.svg)]()
[![Frontend Coverage](https://img.shields.io/badge/Frontend%20Coverage-75%2B%25-brightgreen.svg)]()

## 📋 Descrição / Description

**PT-BR:** O FórumHub é uma plataforma de fórum moderna e robusta, desenvolvida para facilitar a troca de conhecimento. O projeto foca em excelência técnica, utilizando uma arquitetura de Monolito Modular, seguindo rigorosamente os princípios SOLID, Clean Code e a metodologia TDD (Test Driven Development).

**EN:** FórumHub is a modern and robust forum platform designed to facilitate knowledge sharing. The project focuses on technical excellence, utilizing a Modular Monolith architecture and strictly following SOLID principles, Clean Code, and TDD (Test Driven Development).

---

## 📚 Documentação Completa / Full Documentation

Acesse os detalhes por idioma / Access details by language:

### [🇧🇷 Português](./docs/pt-br/README.md)
1.  [Introdução](./docs/pt-br/01-introducao.md)
2.  [Arquitetura](./docs/pt-br/02-arquitetura.md)
3.  [Infraestrutura e Docker](./docs/pt-br/03-infraestrutura.md)
4.  [Documentação da API](./docs/pt-br/04-api.md)
5.  [Frontend (Vue.js)](./docs/pt-br/05-frontend.md)
6.  [Estratégia de Testes](./docs/pt-br/06-testes.md)

### [🇺🇸 English](./docs/en/README.md)
1.  [Introduction](./docs/en/01-introduction.md)
2.  [Architecture](./docs/en/02-architecture.md)
3.  [Infrastructure and Docker](./docs/en/03-infrastructure.md)
4.  [API Documentation](./docs/en/04-api.md)
5.  [Frontend (Vue.js)](./docs/en/05-frontend.md)
6.  [Testing Strategy](./docs/en/06-testing.md)

---

## 🚀 Quick Start

### Pré-requisitos / Prerequisites
- Docker & Docker Compose
- Git

### Como executar / How to run

**PT-BR:**
1. Clone o repositório.
2. Execute o script de automação: `./automation/run.sh`
3. Escolha a opção **4 (Full Stack)**.
4. Acesse: Backend em `localhost:8080` | Frontend em `localhost:3000`.

**EN:**
1. Clone the repository.
2. Run the automation script: `./automation/run.sh`
3. Choose option **4 (Full Stack)**.
4. Access: Backend at `localhost:8080` | Frontend at `localhost:3000`.

---

## 🛠️ Tecnologias / Tech Stack

- **Backend:** Java 17, Spring Boot 3, Hibernate, Flyway, Spring Security (JWT).
- **Frontend:** Vue.js 3 (Composition API), TypeScript, Pinia, Vuetify 3, Vitest.
- **Infra:** Docker, MySQL, Spring Actuator.
- **Tests:** JUnit 5, Mockito, TestContainers, Cypress.