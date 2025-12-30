# 10 - Plano de Melhorias e Dívida Técnica

Este documento lista os pontos de melhoria identificados no escopo atual do projeto, visando consolidar a qualidade, segurança e manutenibilidade do FórumHub.

## 🚨 Prioridade Alta (Segurança & Estabilidade)

### 1. Gestão de Segredos (Secrets Management)
- **Problema:** Atualmente, segredos como `API_SECURITY_TOKEN_SECRET` e senhas de banco estão hardcoded no `docker-compose.yml`.
- **Solução:** Implementar leitura de variáveis de ambiente (`.env`) não versionadas.
- **Impacto:** Evita vazamento de credenciais em repositórios públicos.

### 2. Serialização de Paginação (Spring Data)
- **Problema:** Logs indicam aviso sobre a serialização instável de `PageImpl` (`Serializing PageImpl instances as-is is not supported`).
- **Solução:** Configurar `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` ou migrar para `PagedModel` oficial do Spring HATEOAS.
- **Impacto:** Garante estabilidade na estrutura JSON de listas paginadas para o frontend.

## 🛠️ Prioridade Média (Manutenibilidade & DX)

### 3. Documentação API (Swagger/OpenAPI)
- **Problema:** Os DTOs (`TopicRequestDTO`, etc.) carecem de anotações `@Schema`.
- **Solução:** Adicionar descrições e exemplos aos campos dos DTOs.
- **Impacto:** Melhora a usabilidade do Swagger UI para desenvolvedores front-end e terceiros.

### 4. Internacionalização (i18n)
- **Problema:** Mensagens de erro no Backend (`GlobalExceptionHandler`) e textos do Frontend estão hardcoded em Português.
- **Solução:**
  - **Backend:** Implementar `MessageSource` com arquivos `.properties` (messages_pt.properties, messages_en.properties).
  - **Frontend:** Implementar `vue-i18n`.
- **Impacto:** Permite que o sistema atenda usuários globais, alinhando-se com a documentação bilíngue.

## ⚡ Prioridade Baixa (Otimização)

### 5. Otimização de Build Frontend
- **Problema:** O build do Vite alerta sobre chunks maiores que 500kB.
- **Solução:** Configurar `rollupOptions` no `vite.config.ts` para dividir chunks de vendor (separar `vuetify` e `vue` do bundle principal).
- **Impacto:** Melhora o tempo de carregamento inicial da aplicação.

### 6. Padronização de Tratamento de Erros (RFC 7807)
- **Problema:** O formato de erro atual é customizado.
- **Solução:** Adotar o padrão **Problem Details for HTTP APIs (RFC 7807)**.
- **Impacto:** Aumenta a interoperabilidade com clientes HTTP padrão.
