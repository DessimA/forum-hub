# 05 - Frontend (Vue.js 3)

## 🎨 Design System: Clean & Dark
The FórumHub frontend has evolved to a professional and balanced **Clean Dark** aesthetic.

### Applied Design Principles:
- **Sober Visuals:** Predominant use of dark tones (`#0a0e27`) with strategic transparencies to reveal the background image.
- **Functional Neon:** The use of neon colors (`#00f0ff`) has been reduced and now acts as a subtle visual guide, highlighting only essential elements such as top borders of active cards or primary action buttons.
- **Immersive Background:** Implementation of a background image (`background.jpg`) with dynamic overlay to ensure depth without compromising legibility.
- **Typography:** Roboto as the primary font for clarity and modernity.

## 🏗️ Code Structure
We utilize **Vue 3** with **Composition API** and **TypeScript**:
- **Views:** High-level pages in `src/views/`. Examples: `TopicListView`, `TopicDetailView`, `UserManagementView`.
- **Components:** Layout and common components in `src/components/`.
- **Stores (Pinia):** Modularized state management (`auth.ts`, `topic.ts`, `course.ts`).
- **Services:** API abstraction layer using **Axios**, centralized in `src/services/api.ts`.

## 🛠️ Key Technologies
- **Vue.js 3:** Reactive framework.
- **Vuetify 3:** Component library based on Material Design.
- **Vite:** Ultra-fast build tool and development server.
- **Pinia:** Official state store for Vue.
- **Vitest:** Unit testing framework.

## 🔑 Authentication and Security Flow
1. **Login:** User sends `email` and `password`.
2. **Token:** Backend returns a JWT which is persisted in `localStorage`.
3. **State Persistence:** `AuthStore` extracts token information (email, roles) via Base64 decoding for instantaneous UI control.
4. **Interceptors:** All Axios requests automatically include the `Authorization: Bearer {token}` header.

## 📁 Conventions
- **Naming:** Component files in `PascalCase` (`TheNavbar.vue`), views in `PascalCase` (`TopicListView.vue`).
- **Language Standard:** All code, variables, and field names follow the **English** standard (e.g., `password` instead of `senha`, `name` instead of `nome`).