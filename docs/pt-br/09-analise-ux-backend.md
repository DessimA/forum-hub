# 11 - Análise de Integração Backend/Frontend

Este documento detalha as funcionalidades expostas pelo Backend que estão sendo subutilizadas ou ignoradas pela interface atual do Frontend, representando oportunidades de melhoria de UX sem custo de desenvolvimento de API.

## 🔍 Oportunidades de Otimização

### 1. Filtragem e Busca de Tópicos (Crítico)
- **Backend:** Possui estrutura de banco otimizada com índices em `status`, `course_id` e `author_id`. Suporta consultas eficientes.
- **Frontend Atual:** Exibe uma lista única paginada, sem opções de filtro.
- **Potencial Inexplorado:**
  - **Filtro por Curso:** O usuário não consegue ver apenas dúvidas de "Java" ou "Vue.js", embora o endpoint de listagem de cursos exista e os tópicos tenham essa relação.
  - **Filtro por Status:** Não há como ver apenas tópicos "Não Resolvidos", dificultando para instrutores encontrarem onde ajudar.
  - **Busca Textual:** O banco suporta `LIKE` no título, mas não há barra de busca na UI.

### 2. Ordenação Dinâmica
- **Backend:** Suporte nativo do Spring Data Pageable para parâmetros `?sort=field,desc`.
- **Frontend Atual:** Ordem fixa (provavelmente inserção).
- **Potencial Inexplorado:**
  - Adicionar dropdown: "Mais Recentes", "Antigos", "Por Status".
  - Permitir que o usuário controle a relevância do conteúdo.

### 3. Feedback de Validação e UX
- **Backend:** Possui validações ricas (`@NotBlank`, validação de duplicidade, regras de hierarquia de roles).
- **Frontend Atual:** Utiliza `alert()` e `prompt()` nativos do navegador para ações críticas (ex: Bloqueio de usuário, Exclusão de log).
- **Potencial Inexplorado:**
  - Substituir `prompt` por Dialogs do Vuetify (`v-dialog`) para capturar justificativas de forma elegante.
  - Exibir erros de validação do Backend (ex: "Tópico duplicado") diretamente nos campos do formulário (`v-text-field :error-messages`) em vez de alertas genéricos.

### 4. Gestão de Bloqueios (Moderador)
- **Backend:** Permite bloqueio temporário com data de expiração exata.
- **Frontend Atual:** Pede "dias" via prompt simples.
- **Potencial Inexplorado:**
  - Interface com `Date Picker` para selecionar a data exata de desbloqueio, aproveitando o campo `blocked_until` do banco.

## 🎯 Recomendação Imediata
Priorizar a implementação da **Barra de Filtros na Listagem de Tópicos** (`TopicListView`). Isso aumentará drasticamente a usabilidade do sistema utilizando endpoints que já existem e estão testados.
