# 01 - Introdução ao FórumHub

## 📖 Sobre o Projeto
O **FórumHub** é uma plataforma de fórum educacional de alto desempenho e segurança proativa. O sistema permite uma interação fluida entre alunos e instrutores, com foco total na rastreabilidade de ações e integridade dos dados.

O projeto utiliza uma arquitetura de **Monólito Modular**, garantindo que a lógica de negócio seja isolada e fácil de manter, enquanto a interface entrega uma experiência de usuário moderna e responsiva.

---

## 📂 Estrutura de Diretórios
Organização oficial do repositório:

```text
forum-hub/
├── automation/             # Scripts públicos de execução e testes
│   ├── run.sh              # CLI Manager para Docker (Menu interativo)
│   └── test.sh             # Runner unificado de testes (Maven + Vitest)
│
├── backend/                # API REST Spring Boot 3.4.1
│   ├── src/main/java/com/forumhub/
│   │   ├── modules/        # Domínios (User, Topic, Course, Answer, Audit)
│   │   ├── security/       # Defesa em profundidade e JWT
│   │   └── exception/      # Tratamento global de erros (Business & Integrity)
│   └── src/main/resources/db/migration # Versionamento Flyway
│
├── frontend/               # SPA Vue.js 3.5 + Vuetify 3.11
│   ├── src/
│   │   ├── stores/         # Pinia (Estado global de busca e auth)
│   │   ├── views/          # Interfaces baseadas em Diálogos
│   │   └── services/       # Cliente API Axios
│   └── vite.config.ts      # Configuração de build e Vitest 4
│
└── infra/                  # Infraestrutura (Docker & Nginx)
```

---

## 🚀 Funcionalidades Principais (Estado Atual)

| Módulo | Funcionalidade | Descrição |
| :--- | :--- | :--- |
| **Busca Avançada** | Filtros & Sort | Busca por título, curso, status e ano com ordenação dinâmica. |
| **Ciclo de Vida** | Lock/Unlock | Autores e Moderadores podem fechar e reabrir discussões. |
| **Moderação** | Auto-Cleanup | Punições a usuários removem automaticamente o conteúdo ofensivo. |
| **Segurança** | Real-time Check | Verificação de bloqueio em tempo real a cada requisição API. |
| **Auditoria** | Restore Total | Recuperação de tópicos e respostas preservando UUIDs originais. |
| **Interface** | Modern UX | Sistema 100% baseado em Diálogos e Snackbars do Vuetify. |
