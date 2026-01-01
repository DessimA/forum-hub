# FórumHub

[![Project Status: Active](https://img.shields.io/badge/Project%20Status-Active-brightgreen.svg)]()
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Java](https://img.shields.io/badge/Java-17-orange.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green.svg)]()
[![Vue.js](https://img.shields.io/badge/Vue.js-3.5-4FC08D.svg)]()
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)]()

## 📋 Descrição / Description

**PT-BR:** O FórumHub é uma plataforma moderna para troca de conhecimento focada em **segurança proativa** e **integridade de dados**. Utiliza arquitetura de Monólito Modular com IDs baseados em UUIDs determinísticos. A aplicação conta com um sistema de moderação avançado, autenticação JWT robusta e suporte completo a internacionalização (i18n).

**EN:** ForumHub is a modern knowledge-sharing platform focused on **proactive security** and **data integrity**. It uses a Modular Monolith architecture with deterministic UUID-based IDs. The application features an advanced moderation system, robust JWT authentication, and full internationalization (i18n) support.

---

## ✨ Recursos de Destaque / Key Features

- **🛡️ Segurança Avançada:**
  - Gestão de segredos via variáveis de ambiente (`.env`).
  - Swagger protegido (Bearer Auth) e desabilitado em produção.
  - Bloqueio proativo de usuários com limpeza automática de conteúdo.
- **🌍 Internacionalização (i18n):**
  - Backend responde mensagens de erro em Inglês ou Português baseado no header `Accept-Language`.
- **🏗️ Arquitetura Robusta:**
  - Monólito Modular bem definido.
  - UUIDs (Binary 16) para alta performance de banco.
  - Paginação estável serializada via DTO.
- **⚡ Frontend Otimizado:**
  - Build com *Manual Chunking* para carregamento rápido.
  - Interface moderna com Material Design e tema Neon.

---

## 🛠️ Tecnologias / Tech Stack

- **Backend:** Java 17, Spring Boot 3.4.1, Flyway, Spring Security (JWT), MapStruct, SpringDoc OpenAPI.
- **Frontend:** Vue.js 3.5+, TypeScript, Pinia 2.3, Vuetify 3.11, Vite 6.
- **Tests:** JUnit 5, Mockito, Vitest 4, Vue Test Utils.
- **Infra:** Docker, Nginx, MySQL 8.4.

---

## 🚀 Quick Start

### Pré-requisitos
- Docker & Docker Compose instalados.

### Passo a Passo

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/forum-hub.git
   cd forum-hub
   ```

2. **Configure o Ambiente (.env):**
   É **obrigatório** criar o arquivo de variáveis de ambiente.
   ```bash
   cp .env.example .env
   ```
   *Edite o arquivo `.env` se desejar alterar senhas ou segredos.*

3. **Execute o Projeto:**
   Utilize o gerenciador CLI para facilitar o processo:
   ```bash
   ./automation/run.sh
   ```
   *Selecione a opção **3. Executar Ambos (Full Stack)** no menu.*

4. **Acesse:**
   - **Frontend:** `http://localhost:3000` (Dev) ou `http://localhost:80` (Prod)
   - **Swagger UI:** `http://localhost:8080/swagger-ui.html`

---

## 📚 Documentação Completa

A documentação técnica detalhada está disponível na pasta `/docs`:

- [02 - Arquitetura de Software](./docs/pt-br/02-arquitetura.md) (Diagramas C4 e Decisões)
- [03 - Infraestrutura e Docker](./docs/pt-br/03-infraestrutura.md) (Setup e Scripts)
- [04 - API REST](./docs/pt-br/04-api.md) (Endpoints e Segurança)
- [05 - Frontend](./docs/pt-br/05-frontend.md)
- [06 - Testes](./docs/pt-br/06-testes.md)

---

## 🤝 Contribuindo

1.  Faça um Fork do projeto.
2.  Crie uma Branch para sua Feature (`git checkout -b feature/AmazingFeature`).
3.  Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`).
4.  Push para a Branch (`git push origin feature/AmazingFeature`).
5.  Abra um Pull Request.

---

## 📄 Licença

Distribuído sob a licença MIT. Veja `LICENSE` para mais informações.