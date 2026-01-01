# 01 - Introduction

**ForumHub** is a Full Stack application designed as a backend challenge to simulate the behavior of a discussion forum (similar to Alura's). The project focuses on **Proactive Security**, **Clean Architecture**, and **Developer Experience (DX)**.

## 🎯 Objective
To provide a stable and secure platform where users can create topics, answer questions, and moderators can maintain community integrity through advanced auditing tools.

## 🚀 Core Technologies
- **Backend:** Java 17 with Spring Boot 3.4.
- **Frontend:** Vue.js 3 with Vuetify 3.
- **Database:** MySQL 8.4 with Flyway Migrations.
- **Security:** Spring Security + JWT.
- **Infrastructure:** Docker & Docker Compose.

## 🌟 Highlights
1. **Full Auditing:** Every deletion generates a log that allows for full resource restoration, including its original UUID.
2. **Proactive Security:** Real-time block checking, instantly invalidating tokens of punished users.
3. **Native i18n:** Backend ready to respond in multiple languages.
4. **Optimized Build:** Frontend configured for maximum loading performance.