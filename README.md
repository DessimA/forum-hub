# FórumHub

[![Project Status: Active](https://img.shields.io/badge/Project%20Status-Active-brightgreen.svg)]()
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Backend Coverage](https://img.shields.io/badge/Backend%20Coverage-85%2B%25-brightgreen.svg)]()
[![Frontend Coverage](https://img.shields.io/badge/Frontend%20Coverage-80%2B%25-brightgreen.svg)]()

## 📋 Descrição / Description

**PT-BR:** O FórumHub é uma plataforma moderna para troca de conhecimento focada em **segurança proativa** e **integridade de dados**. Utiliza arquitetura de Monolito Modular com IDs baseados em UUIDs determinísticos. A aplicação conta com um sistema de moderação avançado que automatiza a remoção de conteúdo ofensivo e permite a recuperação total via auditoria.

**EN:** ForumHub is a modern knowledge-sharing platform focused on **proactive security** and **data integrity**. It uses a Modular Monolith architecture with deterministic UUID-based IDs. The application features an advanced moderation system that automates the removal of offensive content and allows full recovery through auditing logs.

---

## ✨ Recursos de Destaque / Key Features

- **Busca e Filtragem:** Busca textual por título, filtragem por curso, status e ordenação dinâmica.
- **Ciclo de Vida:** Fechamento e reabertura de tópicos controlado por autor/moderador.
- **Moderação Inteligente:** Bloqueio de usuários com remoção automática de tópicos ou respostas vinculadas.
- **Auditoria & Recuperação:** Registro detalhado de exclusões permitindo restauração fiel do banco de dados (inclusive UUIDs).
- **UX Moderna:** Interface 100% baseada em Diálogos e Snackbars (sem popups nativos).

---

## 🛠️ Tecnologias / Tech Stack

- **Backend:** Java 17, Spring Boot 3.4.1, Flyway, Spring Security (JWT), MapStruct.
- **Frontend:** Vue.js 3.5+, TypeScript, Pinia 2.3, Vuetify 3.11, Vite 6.
- **Tests:** JUnit 5, Mockito, Vitest 4, Vue Test Utils.
- **Infra:** Docker, Nginx, MySQL 8.4.

---

## 📚 Documentação / Documentation

Consulte a [Documentação Técnica Completa](./docs/pt-br/README.md) para detalhes de arquitetura, API e Banco de Dados.

---

## 🚀 Como Executar / How to Run

```bash
# Clone o projeto e use o CLI Manager
./automation/run.sh
```
Siga o menu para selecionar o Profile e iniciar os serviços.
