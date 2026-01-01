# 06 - Detalhamento da Estratégia de Testes

O FórumHub utiliza uma pirâmide de testes robusta para garantir a estabilidade das regras de negócio, a segurança dos dados e a integridade da interface.

## 🏗️ Backend (Java & Spring Boot)

### 1. Testes de Segurança e Autenticação
Localizados em `com.forumhub.security.*`, estes testes validam o perímetro defensivo da aplicação.
- **Sessão Ativa vs Bloqueio:** Garante que se um administrador bloquear um usuário, o acesso dele seja revogado **imediatamente**, mesmo que o token JWT ainda seja válido.
- **Validação de Token:** Testes de integridade do JWT, expiração e assinatura (HMAC256).

### 2. Ciclo de Vida e Consistência
Localizados em `com.forumhub.modules.topic.*`.
- **Integridade de Status:** Valida transições de estado (ABERTO -> RESOLVIDO).
- **Prevenção de Duplicatas:** Bloqueio de tópicos idênticos.
- **Filtragem Dinâmica:** Validação de filtros complexos (curso, status, busca).

---

## 🖥️ Frontend (Vue.js & Vitest)

### 1. Gestão de Estado (Store)
Localizados em `src/stores/__tests__/*`.
- **Consistência:** Valida que a Store reseta a paginação ao aplicar filtros.
- **Segurança:** Garante limpeza do `localStorage` ao deslogar.

### 2. Componentes e UI
Localizados em `src/views/__tests__/*`.
- **Feedback Visual:** Validação de spinners de carregamento e mensagens de erro.
- **Mocks:** Uso de JSDOM para simular comportamento do navegador.

---

## 🚀 Execução Unificada e Segura

O script `./automation/test.sh` orquestra a execução das suítes em containers isolados.

### ⚠️ Requisito Crítico: Variáveis de Ambiente
Os testes **dependem** do arquivo `.env` na raiz do projeto para configurar as credenciais do banco de dados de teste e segredos JWT.
- O script utiliza a flag `--env-file` do Docker Compose para injetar essas variáveis com segurança.
- **Nunca** execute os testes sem antes criar o arquivo `.env` (use `.env.example` como base).

### Relatório Final
O script consolida os resultados do Maven (Backend) e Vitest (Frontend), retornando sucesso apenas se **ambos** passarem.
