# Introdução Detalhada - FórumHub

## O Desafio
O FórumHub nasceu de um desafio proposto no programa **Oracle Next Education (ONE)** em parceria com a **Alura**. O objetivo inicial era criar uma API REST para gerenciar tópicos de um fórum, mas o projeto foi expandido para uma aplicação **Full-Stack Enterprise** completa.

## Filosofia de Desenvolvimento
Este projeto não foca apenas em funcionalidade, mas em **excelência técnica**. Foram aplicados rigorosamente:
- **TDD (Test Driven Development):** Testes escritos antes da implementação lógica.
- **SOLID:** Princípios de design para garantir código sustentável e desacoplado.
- **Clean Code:** Nomenclatura semântica, funções pequenas e legibilidade máxima.
- **DRY (Don't Repeat Yourself):** Abstração de lógicas repetitivas em serviços e utilitários.

## Visão Geral do Ecossistema
A aplicação é dividida em dois grandes blocos que se comunicam de forma assíncrona e segura:

### 1. Backend (O Núcleo)
Construído com **Spring Boot 3**, o backend gerencia toda a lógica de persistência, regras de negócio e segurança. Ele utiliza uma arquitetura em camadas:
- **Controller:** Porta de entrada das requisições, responsável pela validação de entrada (`@Valid`) e documentação Swagger.
- **Service:** Onde a "mágica" acontece. Contém as regras de negócio complexas e orquestração de transações.
- **Repository:** Camada de persistência que utiliza Spring Data JPA para comunicação com MySQL/H2.
- **Security:** Filtros customizados para interceptação de tokens JWT e gestão de autoridades.

### 2. Frontend (A Experiência)
Uma SPA (Single Page Application) moderna com **Vue.js 3**, focada em performance e usabilidade:
- **Composition API:** Organização de lógica reutilizável através de composables.
- **Pinia:** Gerenciamento de estado global centralizado (Auth, Tópicos, Usuários).
- **Vuetify 3:** Interface baseada em Material Design com um tema customizado "Neon Dark".

## Casos de Uso Avançados
- **Ciclo de Vida do Tópico:** Um tópico pode passar de `ABERTO` para `RESOLVIDO` quando o autor marca uma resposta como solução.
- **Sistema de Auditoria:** Cada exclusão de tópico gera um rastro imutável no banco de dados, registrando o autor da ação e a justificativa fornecida.
- **Proteção de Dados:** Nenhuma senha é armazenada em texto claro; utilizamos BCrypt com 12 rounds de hashing.