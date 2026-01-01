# 08 - Roadmap de Melhorias Futuras

Este documento lista as funcionalidades e refinamentos planejados para o FórumHub que ainda não foram implementados.

## 🚀 Próximos Passos (Dívida Técnica e Inovação)

### Prioridade Alta

1. **Rate Limiting (Proteção contra Brute-Force)**
   - **Objetivo:** Implementar limitação de requisições por IP, especialmente nos endpoints de autenticação (`/auth/login`), utilizando Bucket4j ou Redis.
   - **Estado Atual:** Ausente. A API está vulnerável a ataques massivos de dicionário.

2. **Internacionalização do Frontend (Vue-i18n)**
   - **Objetivo:** Permitir que a interface do usuário (botões, menus, labels) mude de idioma dinamicamente entre PT-BR e EN.
   - **Estado Atual:** A interface está com textos fixos em Português.

### Prioridade Média

3. **Logging Estruturado (JSON)**
   - **Objetivo:** Configurar o Logback para gerar logs em formato JSON em produção.
   - **Motivação:** Facilita a ingestão e análise em ferramentas de observabilidade (ELK Stack, Datadog, CloudWatch).
   - **Estado Atual:** Logs em texto plano padrão (Console).

4. **Padronização RFC 7807 (Problem Details)**
   - **Objetivo:** Adotar o padrão oficial "Problem Details for HTTP APIs" para todas as respostas de erro.
   - **Estado Atual:** Formato customizado.

### Prioridade Baixa

5. **Cache de Segundo Nível (L2 Cache)**
   - **Objetivo:** Implementar cache para entidades pouco voláteis (ex: `Course`, `Profile`) usando Caffeine ou Redis.
   - **Motivação:** Reduzir carga no banco de dados em listagens frequentes.

6. **Pipeline de CI/CD (GitHub Actions)**
   - **Objetivo:** Automatizar testes e build a cada push.
   - **Estado Atual:** Scripts manuais (`run.sh`).