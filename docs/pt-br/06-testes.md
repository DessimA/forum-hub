# 06 - Estratégia de Testes

## 🧪 Metodologia
Adotamos o **TDD (Test Driven Development)** rigoroso. A estabilidade do sistema é garantida por uma suite que cobre desde regras de negócio até segurança.

## 📊 Níveis de Teste Implementados

### Backend (Java)
- **Unitários:** Cobertura extensiva de `Services` e `Mappers` usando **Mockito**.
- **Integração:** Validação de fluxos complexos usando banco **H2 em modo MySQL**.
- **Segurança:** Testes específicos para validar o bloqueio de usuários (`SuspendedUserTest`) e hierarquia de permissões.
- **Auditoria:** Testes de recuperação de dados garantem que a lógica de restauração por UUID funcione perfeitamente.

### Frontend (Vue.js)
- **Unitários:** Testes de Stores Pinia usando **Vitest**.
- **Componentes:** Validação de UI e eventos com **Vue Test Utils**.
- **Tipagem:** Verificação estática via **TypeScript** integrada ao processo de build para prevenir erros em runtime.

## 🚀 Execução Automatizada
Utilizamos Docker para garantir que os testes rodem em um ambiente idêntico à produção:
`./automation/test.sh`

## 📈 Métricas de Qualidade
- **Cobertura Backend:** > 80% (foco em lógica crítica).
- **Cobertura Frontend:** > 75% (foco em estados de store e componentes de layout).