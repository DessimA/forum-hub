# 05 - Frontend (Vue.js 3)

O frontend do FórumHub prioriza a clareza e a eficiência, utilizando as tecnologias mais recentes do ecossistema Vue.

## 🎨 User Experience (UX)
A interface é construída inteiramente sobre o **Vuetify 3**, garantindo que todas as interações críticas ocorram através de componentes integrados.

### Padrões de Interface:
- **Navegação Dinâmica:** Sincronização de filtros e ordenação diretamente com a URL.
- **Fluxos Modais:** Ações de edição e exclusão utilizam `v-dialog`.
- **Feedback Não Intrusivo:** Notificações via `v-snackbar`.

---

## 🏗️ Gestão de Estado (Pinia)
O estado é descentralizado em stores:
- **TopicStore:** Preserva filtros de `status`, `courseId` e `title` durante a paginação.
- **AuthStore:** Gerencia o token JWT e decodifica roles para controle de acesso visual.

---

## ⚡ Performance e Build

### Otimização de Bundles (Manual Chunking)
Para garantir um carregamento inicial rápido, o `vite.config.ts` foi configurado para dividir o código em chunks estratégicos:

```mermaid
graph TD
    Main[index.html] -->|Loads| VendorVue[vendor-vue.js]
    Main -->|Loads| VendorVuetify[vendor-vuetify.js]
    Main -->|Loads| AppCode[index.js (App Logic)]
    
    VendorVue --> VueCore
    VendorVue --> Pinia
    VendorVue --> Router
    
    VendorVuetify --> VuetifyLib
    VendorVuetify --> Icons
```

Isso impede que bibliotecas pesadas (como o Vuetify) sejam rebaixadas toda vez que apenas a lógica da aplicação muda, aproveitando melhor o cache do navegador.

### Segurança
- **Integração com API:** Todas as chamadas HTTP passam por um interceptor em `src/services/api.ts` que anexa automaticamente o Token Bearer.
- **Tratamento de 401:** Redirecionamento automático para login em caso de token expirado.
