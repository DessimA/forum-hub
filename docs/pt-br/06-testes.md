# 06 - Detalhamento da Estratégia de Testes

O FórumHub utiliza uma pirâmide de testes robusta para garantir a estabilidade das regras de negócio, a segurança dos dados e a integridade da interface.

## 🏗️ Backend (Java & Spring Boot)

### 1. Testes de Segurança e Autenticação
Localizados em `com.forumhub.security.*`, estes testes validam o perímetro defensivo da aplicação.
- **Sessão Ativa vs Bloqueio:** Garante que se um administrador bloquear um usuário, o acesso dele seja revogado **imediatamente**, mesmo que o token JWT ainda seja válido (Verificação em tempo real no banco).
- **Validação de Token:** Testes de integridade do JWT, expiração e assinatura (HMAC256).
- **Hierarquia de Cargos:** Validação de que um `MODERATOR` pode suspender um `USER`, mas é impedido de realizar qualquer ação contra um `ADMIN`.

### 2. Ciclo de Vida e Consistência de Tópicos
Localizados em `com.forumhub.modules.topic.*`.
- **Integridade de Status:** Se a única resposta marcada como **Solução** for excluída, o sistema deve automaticamente reverter o status do tópico de `RESOLVED` para `OPEN`.
- **Prevenção de Duplicatas:** Bloqueio de tópicos com título e mensagem idênticos para evitar spam e redundância.
- **Filtragem Dinâmica:** Validação de que os filtros de curso, status e busca textual retornam apenas os subconjuntos de dados corretos.

### 3. Moderação Contextual e Auditoria
Localizados em `com.forumhub.modules.audit.*` e `com.forumhub.modules.user.*`.
- **Backup em Cascata:** Valida que, ao excluir um tópico por moderação, todas as respostas vinculadas também geram logs de auditoria individuais antes de serem removidas por cascata no banco.
- **Restauração Fiel:** Garante que um item restaurado recupere seu **UUID original**, mantendo a integridade de links externos e referências cruzadas.
- **Auto-Limpeza:** Testa se a ação de punir um usuário passando o contexto de uma mensagem remove automaticamente essa mensagem do sistema.

---

## 🖥️ Frontend (Vue.js & Vitest)

### 1. Gestão de Estado (Store)
Localizados em `src/stores/__tests__/*`.
- **Consistência de Filtros:** Valida que a Store do Pinia limpa os parâmetros de busca e reseta a paginação para a página zero ao aplicar um novo filtro.
- **Fluxo de Logout:** Garante que o `localStorage` e o estado da aplicação sejam completamente destruídos ao deslogar, prevenindo vazamento de dados entre usuários no mesmo navegador.

### 2. Componentes e UI
Localizados em `src/views/__tests__/*` e `src/components/__tests__/*`.
- **Estados de Carregamento (Loading):** Validação de que o spinner visual aparece exatamente durante o tempo de requisição e desaparece após a resposta.
- **Renderização de Status:** Garante que as cores e rótulos (`ABERTO`, `FECHADO`, `RESOLVIDO`) correspondam fielmente aos dados vindos da API.
- **Mocks de Browser:** Implementação de mocks para `ResizeObserver` e `IntersectionObserver` para garantir que componentes complexos do Vuetify se comportem de forma determinística no ambiente de teste (JSDOM).

---

## 🚀 Execução Unificada
O script `./automation/test.sh` orquestra a execução de todas as suítes em containers isolados, garantindo que o ambiente de teste seja uma réplica exata da produção.
- **Relatório Final:** O script consolida os códigos de saída do Maven e do Vitest para fornecer um veredito unificado sobre a saúde do projeto.