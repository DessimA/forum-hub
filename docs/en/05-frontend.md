# 05 - Frontend (Vue.js 3)

The ForumHub frontend prioritizes clarity and efficiency, utilizing the latest technologies from the Vue ecosystem.

## 🎨 User Experience (UX)
The interface is built entirely on **Vuetify 3**, ensuring all critical interactions occur through integrated components without the use of native browser windows.

### UI Patterns:
- **Dynamic Navigation:** The topic listing features an integrated toolbar that synchronizes search state (filters and sorting) directly with the URL and Store.
- **Workflows:** Moderation, editing, and deletion actions use `v-dialog` with real-time field validation.
- **Notifications:** Success and failure of operations (e.g., "Reply Sent", "Permission Error") are communicated via `v-snackbar` non-intrusively.

---

## 🏗️ State Management (Pinia)
Application state is decentralized into specialized stores:
- **TopicStore:** Manages search complexity. Filters for `status`, `courseId`, and `title` are preserved during pagination to maintain context.
- **AuthStore:** Decodes the JWT to manage visual hierarchy (hiding moderation buttons for regular users).

---

## 🔒 Stability and Performance
- **Vite 6:** Configured with `inline dependencies` to correctly process Vuetify CSS during tests.
- **Nginx Cache Busting:** Infrastructure settings ensure `index.html` is never cached, preventing script loading errors after new deployments (MIME type errors).
