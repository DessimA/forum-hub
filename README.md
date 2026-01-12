# FórumHub

[![Project Status: Active](https://img.shields.io/badge/Project%20Status-Active-brightgreen.svg)]()
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Java](https://img.shields.io/badge/Java-17-orange.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green.svg)]()
[![Vue.js](https://img.shields.io/badge/Vue.js-3.5-4FC08D.svg)]()
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)]()

O **FórumHub** é uma plataforma Full Stack de última geração para troca de conhecimento, desenvolvida com foco em **Segurança Proativa**, **Arquitetura Limpa** e uma experiência de usuário imersiva.

---

## 📋 Sobre o Projeto

O FórumHub utiliza uma arquitetura de **Monólito Modular**, garantindo que cada domínio (Tópicos, Usuários, Cursos, Notificações) seja independente e fácil de manter.

### ✨ Diferenciais de Destaque
- **🎨 Design Deep Dark:** Interface otimizada para produtividade com fundo Pure Black (#000000) e acentos Sky Blue (#38BDF8).
- **🛡️ Auditoria Avançada:** Sistema que registra snapshots de dados excluídos, permitindo que administradores restaurem recursos ao estado original.
- **📱 Mobile-First:** Experiência fluida em qualquer dispositivo com menus adaptativos e componentes Vuetify 3 customizados.
- **🚀 Performance:** Utilização de cache Redis L2 e UUIDs binários para garantir respostas rápidas mesmo com grande volume de dados.

---

## 📚 Documentação Técnica

Para detalhes profundos sobre como o sistema foi construído, consulte nossos guias especializados:

- [📖 Introdução Geral](./docs/pt-br/01-introducao.md)
- [🏗️ Arquitetura e Decisões (ADRs)](./docs/pt-br/02-arquitetura.md)
- [📡 API REST e Contratos](./docs/pt-br/04-api.md)
- [🖥️ Frontend e Design System](./docs/pt-br/05-frontend.md)
- [🧪 Estratégia de Testes (Unit & E2E)](./docs/pt-br/06-testes.md)
- [🗄️ Modelagem de Banco de Dados](./docs/pt-br/07-banco-dados.md)

---

## 🚀 Como Executar

### Passo a Passo Rápido
1.  **Clone o repositório:** `git clone https://github.com/DessimA/forum-hub.git`
2.  **Configuração:** Copie o `.env.example` para `.env` e ajuste suas chaves.
3.  **Docker:** Execute `./automation/run.sh` e escolha a opção **3**.
4.  **Acesse:** Frontend em `http://localhost:3000` | API em `http://localhost:8080/swagger-ui.html`.

---

## 👨‍💻 Autor

Este projeto foi desenvolvido com dedicação por **José Anderson da Silva Costa**.

- 🌐 **Portfolio/Links:** [meus-links-olive.vercel.app](https://meus-links-olive.vercel.app/)
- 🐙 **GitHub:** [DessimA](https://github.com/DessimA)
- 💼 **LinkedIn:** [in/dessim](https://www.linkedin.com/in/dessim)

---

*"O conhecimento só é valioso quando é compartilhado e protegido."*
