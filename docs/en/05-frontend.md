# 05 - Frontend (Vue.js 3)

## 🎨 Design System
The frontend uses an **Elegant Dark** theme with **Neon Blue** accents, inspired by futuristic interfaces. The base component library is **Vuetify 3**.

## 🏗️ Code Structure
We follow the Composition API pattern:
- **Views:** Full pages (e.g., `LoginView`, `TopicListView`).
- **Components:** Reusable parts (e.g., `TheNavbar`, `TopicCard`).
- **Stores (Pinia):** Global state management (Auth and Topics).
- **Services (Axios):** API call isolation.

## 🔑 Authentication Flow
1. The user logs in.
2. The JWT token is stored in `localStorage`.
3. `AuthStore` decrypts the token payload (Base64) to check user permissions in real-time without querying the backend for every click.
