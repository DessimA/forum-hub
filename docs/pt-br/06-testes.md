# 06 - Estratégia de Testes

## 🧪 TDD (Test Driven Development)
Neste projeto, nenhum código foi implementado sem antes possuir um teste que validasse seu comportamento esperado.

## 📊 Níveis de Teste

### Backend
1. **Unitários (JUnit + Mockito):** Testam a lógica de negócio isolada nos Services.
2. **Integração (H2):** Testam a comunicação com o banco de dados e filtros de segurança.
3. **E2E (TestContainers):** Testam o fluxo completo utilizando um container MySQL real.

### Frontend
1. **Unitários (Vitest):** Testam as Stores e funções utilitárias.
2. **Componentes (Vue Test Utils):** Testam se os componentes renderizam e reagem a eventos corretamente.
3. **E2E (Cypress):** Simulam um usuário real navegando e interagindo com a interface.

## 📈 Metas de Cobertura
- **Backend:** Mínimo de 80%.
- **Frontend:** Mínimo de 75%.
