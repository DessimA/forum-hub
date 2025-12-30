# 05 - Frontend (Vue.js 3)

## 🎨 Design System: Clean & Dark
O frontend do FórumHub evoluiu para uma estética **Clean Dark** profissional e equilibrada. 

### Princípios de Design Aplicados:
- **Visual Sóbrio:** Uso predominante de tons escuros (`#0a0e27`) com transparências estratégicas para revelar a imagem de fundo.
- **Neon Funcional:** O uso de cores neon (`#00f0ff`) foi reduzido e agora atua como um guia visual sutil, destacando apenas elementos essenciais como bordas superiores de cards ativos ou botões de ação principal.
- **Fundo Imersivo:** Implementação de uma imagem de fundo (`background.jpg`) com overlay dinâmico para garantir profundidade sem comprometer a legibilidade.
- **Tipografia:** Roboto como fonte principal para clareza e modernidade.

## 🏗️ Estrutura do Código
Utilizamos **Vue 3** com **Composition API** e **TypeScript**:
- **Views:** Páginas de alto nível em `src/views/`. Exemplos: `TopicListView`, `TopicDetailView`, `UserManagementView`.
- **Components:** Componentes de layout e comuns em `src/components/`.
- **Stores (Pinia):** Gerenciamento de estado modularizado (`auth.ts`, `topic.ts`, `course.ts`).
- **Services:** Camada de abstração da API utilizando **Axios**, centralizada em `src/services/api.ts`.

## 🛠️ Tecnologias Principais
- **Vue.js 3:** Framework reativo.
- **Vuetify 3:** Biblioteca de componentes baseada em Material Design.
- **Vite:** Ferramenta de build e servidor de desenvolvimento ultra-rápido.
- **Pinia:** Store de estado oficial para Vue.
- **Vitest:** Framework de testes unitários.

## 🔑 Fluxo de Autenticação e Segurança
1. **Login:** O usuário envia `email` e `password`.
2. **Token:** O backend retorna um JWT que é persistido no `localStorage`.
3. **Persistência de Estado:** O `AuthStore` extrai informações do token (email, roles) via decodificação Base64 para controle de UI instantâneo.
4. **Interceptores:** Todas as requisições via Axios incluem automaticamente o header `Authorization: Bearer {token}`.

## 📁 Convenções
- **Nomenclatura:** Arquivos de componentes em `PascalCase` (`TheNavbar.vue`), views em `PascalCase` (`TopicListView.vue`).
- **Padrão de Linguagem:** Todo o código, variáveis e nomes de campos seguem o padrão **Inglês** (ex: `password` em vez de `senha`, `name` em vez de `nome`).