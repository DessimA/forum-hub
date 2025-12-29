# 02 - Arquitetura do Sistema

## 🏗️ Monolito Modular
O sistema utiliza uma arquitetura de **Monolito Modular**. Isso significa que, embora tudo seja entregue em um único artefato, o código é organizado em módulos independentes com responsabilidades bem definidas.

### Diagrama de Blocos
```mermaid
graph TD
    subgraph "Camada de Apresentação"
        UI[Vue.js SPA]
    end
    
    subgraph "Camada de API (Spring Boot)"
        direction TB
        Auth[Módulo de Autenticação]
        Topic[Módulo de Tópicos]
        Answer[Módulo de Respostas]
        UserM[Módulo de Usuários]
        Audit[Módulo de Auditoria]
    end
    
    subgraph "Persistência"
        DB[(MySQL)]
        Flyway[Flyway Migrations]
    end
    
    UI <--> Auth
    UI <--> Topic
    Topic --> Answer
    Topic --> UserM
    Answer --> UserM
    
    Auth --> DB
    Topic --> DB
    UserM --> DB
    Audit --> DB
```

## 🛡️ Decisões Arquiteturais (ADRs)
As principais decisões do projeto estão documentadas em [ADRs](../decisions/):
1. **ADR 001:** Escolha do Monolito Modular.
2. **ADR 002:** Autenticação Stateless com JWT.
3. **ADR 003:** Auditoria Mandatória.

## 📐 Princípios de Design
- **SOLID:** Cada classe tem uma única responsabilidade. Por exemplo, criamos o `SecurityValidationService` apenas para lidar com regras de autorização, removendo essa carga dos controladores.
- **DRY (Don't Repeat Yourself):** Validações comuns foram movidas para serviços utilitários.
- **Clean Code:** Nomes de métodos revelam sua intenção (ex: `markAsSolution` em vez de `updateStatus`).