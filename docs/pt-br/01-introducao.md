# 01 - Introdução ao FórumHub

## 📖 Sobre o Projeto
O **FórumHub** é uma plataforma de fórum educacional moderna e responsiva. Seu objetivo é permitir que alunos e instrutores interajam através de tópicos de perguntas e respostas, organizados por cursos.

O projeto foi concebido com foco em **Clean Code**, **SOLID** e **Boas Práticas de Engenharia**, servindo não apenas como um produto funcional, mas como uma referência de arquitetura Full Stack robusta.

---

## 📂 Estrutura de Diretórios
A organização do projeto reflete a separação de responsabilidades (Frontend vs Backend vs Infraestrutura).

```text
forum-hub/
├── automation/             # Scripts de automação (CI/CD local)
│   ├── run.sh              # CLI Manager para Docker
│   └── test.sh             # Runner de testes unificado
│
├── backend/                # API REST (Java/Spring Boot)
│   ├── src/main/java/com/forumhub/
│   │   ├── config/         # Configurações (Security, Swagger)
│   │   ├── modules/        # Domínios segregados (User, Topic, Course)
│   │   │   └── [domain]/
│   │   │       ├── controller/  # Endpoints REST
│   │   │       ├── service/     # Regras de Negócio
│   │   │       ├── repository/  # Acesso a Dados
│   │   │       └── dto/         # Contratos de API
│   │   └── security/       # Lógica de Autenticação (JWT)
│   └── src/main/resources/
│       └── db/migration/   # Scripts SQL versionados (Flyway)
│
├── docs/                   # Documentação do Projeto
│   ├── pt-br/              # Documentação em Português
│   └── en/                 # Documentation in English
│
├── frontend/               # Single Page Application (Vue.js)
│   ├── src/
│   │   ├── components/     # Componentes Reutilizáveis (UI)
│   │   ├── views/          # Páginas da Aplicação
│   │   ├── stores/         # Estado Global (Pinia)
│   │   ├── services/       # Integração com API (Axios)
│   │   └── types/          # Definições TypeScript
│   └── vite.config.ts      # Configuração de Build
│
└── infra/                  # Infraestrutura como Código
    ├── backend/            # Dockerfile do Backend
    ├── frontend/           # Dockerfile e Nginx config
    ├── mysql/              # Scripts de init do Banco
    └── docker-compose.yml  # Orquestração dos serviços
```

---

## 🚀 Funcionalidades Principais

| Módulo | Funcionalidade | Descrição |
| :--- | :--- | :--- |
| **Autenticação** | Login/Registro | Acesso seguro via JWT com expiração automática. |
| **Tópicos** | CRUD Completo | Criação, listagem paginada, edição e exclusão de dúvidas. |
| **Respostas** | Interação | Possibilidade de responder tópicos e marcar a solução. |
| **Dashboard** | Admin | Gestão de usuários e logs de auditoria (Role-based). |

---

## 🏁 Primeiros Passos

Para rodar o projeto localmente, consulte o guia de infraestrutura ou use o script de automação:

```bash
./automation/run.sh
```