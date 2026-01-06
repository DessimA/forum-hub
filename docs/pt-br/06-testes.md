# 06 - Testes Automatizados

O **FórumHub** utiliza uma pirâmide de testes abrangente, garantindo a estabilidade tanto do Backend quanto do Frontend.

## 🏛️ Estratégia de Testes

### Backend (Java/Spring Boot)
- **Unitários:** Validação de regras de negócio em isolamento (JUnit 5 + Mockito).
- **Integração:** Validação da persistência (H2 Database) e da camada de segurança.
- **Infraestrutura:** Testes de integração real com **Redis** para validar Rate Limiting e Cache L2.

### Frontend (Vue.js)
- **Unitários:** Testes de componentes e stores (Vitest).
- **E2E (End-to-End):** Fluxos críticos de usuário (Cypress).
  - *Hardening:* Os testes foram otimizados para lidar com animações do Vuetify 3 usando interações forçadas (`{force: true}`) e esperas explícitas por carregamento de dados (timeouts de até 30s).
  - *Dados Dinâmicos:* Uso de `Date.now()` para garantir títulos únicos em criações de tópicos, evitando erros de duplicidade entre execuções.

---

## 🚀 Execução via Docker (Recomendado)

O projeto possui um ambiente de testes isolado que sobe automaticamente os serviços necessários (Redis + H2).

### Executar todos os testes
```bash
./automation/test.sh
```

### Ajustes de Estabilidade (CI/CD)
Para garantir que os testes de alta frequência não sejam bloqueados, o backend no perfil `dev/test` possui um limite de requisições ampliado:
- **Rate Limit:** 1000 requisições por minuto (configurado em `application-dev.yml`).
- **Persistence:** Volumes Docker garantem que o estado do banco seja preservado ou limpo conforme a necessidade do script de automação.

### O que o script faz:
1. Sobe um container de **Redis** dedicado para os testes.
2. Executa os testes de backend no container `backend-test` (USA profile `test`).
3. Executa os testes unitários de frontend no container `frontend-test`.
4. Gera um relatório consolidado no terminal.

---

## 🛠️ Detalhes da Infraestrutura de Teste

### Profile de Teste (`application-test.yml`)
- **Banco de Dados:** H2 Database (In-Memory com modo MySQL).
- **Cache:** Redis real (conectado via rede Docker interna).
- **Rate Limit:** Ajustado para limites menores em testes específicos para validação rápida.

### Resiliência no Redis
Os testes de integração utilizam o utilitário `RestPage` para validar a serialização correta no Redis. Caso o Redis esteja offline, o sistema entra em modo de fallback automático, logando um aviso mas permitindo que a aplicação continue funcionando.

---

## 📊 Cobertura e Qualidade
- **Target Backend:** > 80% de cobertura.
- **Target Frontend:** > 75% de cobertura.
- **Regra de Ouro:** Nenhum código é integrado à branch `main` sem passar em 100% dos testes automatizados.