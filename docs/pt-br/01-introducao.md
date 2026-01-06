# 01 - Introdução

O **FórumHub** é uma aplicação Full Stack projetada como um desafio de backend para simular o comportamento de um fórum de discussões (como o da Alura). O projeto foca em **Segurança Proativa**, **Arquitetura Limpa** e **Experiência do Desenvolvedor (DX)**.

## 🎯 Objetivo
Oferecer uma plataforma estável e segura onde usuários podem criar tópicos, responder dúvidas e moderadores podem manter a integridade da comunidade através de ferramentas avançadas de auditoria.

## 🚀 Principais Tecnologias
- **Backend:** Java 17 com Spring Boot 3.4.
- **Frontend:** Vue.js 3 com Vuetify 3.
- **Banco de Dados:** MySQL 8.4 com Flyway Migrations.
- **Segurança:** Spring Security + JWT.
- **Infraestrutura:** Docker & Docker Compose.

## 🌟 Diferenciais
1. **Design Deep Dark & Mobile-First:** Interface moderna com fundo "Pure Black" (#000000) e acentos "Sky Blue" (#38BDF8), otimizada para dispositivos móveis com menus adaptativos.
2. **Favoritos e Notificações:** Sistema de engajamento que permite favoritar tópicos e receber alertas em tempo real sobre novas respostas e mudanças de status.
3. **Auditoria Completa:** Cada exclusão gera um log que permite a restauração total do recurso, incluindo seu UUID original.
4. **Segurança Proativa:** Verificação de bloqueio em tempo real, invalidando tokens de usuários punidos instantaneamente.
5. **i18n Nativo:** Backend preparado para responder em múltiplos idiomas.
6. **Build Otimizado:** Frontend configurado para máxima performance de carregamento.