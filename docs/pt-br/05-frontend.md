# 05 - Frontend (Vue.js 3)

## 🎨 Design System
O frontend utiliza um tema **Dark Elegante** com detalhes em **Azul Neon**, inspirado em interfaces futuristas. A biblioteca de componentes base é o **Vuetify 3**.

## 🏗️ Estrutura do Código
Seguimos o padrão da Composition API:
- **Views:** Páginas completas (ex: `LoginView`, `TopicListView`).
- **Components:** Partes reutilizáveis (ex: `TheNavbar`, `TopicCard`).
- **Stores (Pinia):** Gerenciamento de estado global (Auth e Topics).
- **Services (Axios):** Isolamento das chamadas de API.

## 🔑 Fluxo de Autenticação
1. O usuário faz login.
2. O token JWT é armazenado no `localStorage`.
3. O `AuthStore` descriptografa o payload do token (Base64) para verificar as permissões do usuário em tempo real sem precisar consultar o backend para cada clique.
