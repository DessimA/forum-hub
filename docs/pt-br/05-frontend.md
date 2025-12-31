# 05 - Frontend (Vue.js 3)

O frontend do FórumHub prioriza a clareza e a eficiência, utilizando as tecnologias mais recentes do ecossistema Vue.

## 🎨 User Experience (UX)
A interface é construída inteiramente sobre o **Vuetify 3**, garantindo que todas as interações críticas ocorram através de componentes integrados, sem o uso de janelas nativas do navegador.

### Padrões de Interface:
- **Navegação Dinâmica:** A listagem de tópicos possui uma barra de ferramentas integrada que sincroniza o estado da busca (filtros e ordenação) diretamente com a URL e a Store.
- **Fluxos de Trabalho:** Ações de moderação, edição e exclusão utilizam `v-dialog` com validação de campo em tempo real.
- **Notificações:** Sucesso e falha de operações (ex: "Resposta Enviada", "Erro de Permissão") são comunicados via `v-snackbar` de forma não intrusiva.

---

## 🏗️ Gestão de Estado (Pinia)
O estado da aplicação é descentralizado em stores especializadas:
- **TopicStore:** Gerencia a complexidade da busca. Filtros de `status`, `courseId` e `title` são preservados durante a paginação para evitar perda de contexto.
- **AuthStore:** Decodifica o JWT para gerenciar a hierarquia visual (escondendo botões de moderação para usuários comuns).

---

## 🔒 Estabilidade e Performance
- **Vite 6:** Configurado com `inline dependencies` para processar corretamente o CSS do Vuetify durante os testes.
- **Nginx Cache Busting:** A configuração de infraestrutura garante que o `index.html` nunca seja cacheado, prevenindo erros de carregamento de scripts após novos deploys (MIME type errors).