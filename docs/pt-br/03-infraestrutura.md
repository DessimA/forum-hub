# 03 - Infraestrutura e Docker

O FórumHub adota uma filosofia **"Infrastructure as Code"** simplificada, onde todo o ambiente necessário para rodar o projeto (banco de dados, backend, frontend) é definido e orquestrado via Docker.

## 🐳 Containerização e Orquestração

Utilizamos `docker-compose` para gerenciar o ciclo de vida da aplicação.

### Serviços Principais

| Serviço | Imagem | Porta Exposta | Função | Variáveis Críticas |
| :--- | :--- | :--- | :--- | :--- |
| **MySQL** | `mysql:8.4` | 3306 | Persistência de dados | `MYSQL_ROOT_PASSWORD` |
| **Backend** | `eclipse-temurin:17` | 8080 | API Spring Boot | `SPRING_DATASOURCE_PASSWORD`, `API_SECURITY_TOKEN_SECRET` |
| **Frontend** | `nginx:stable-alpine` | 80 / 3000 | Servidor Web / Proxy | - |

---

## 🔐 Gerenciamento de Segredos e Configuração

**Segurança em Primeiro Lugar:** Nenhuma senha ou segredo é versionado no Git. O projeto utiliza um arquivo `.env` na raiz para injetar configurações sensíveis nos containers.

### Como Configurar
1. Copie o arquivo de exemplo:
   ```bash
   cp .env.example .env
   ```
2. Edite o `.env` com suas credenciais (ou use os valores padrão para desenvolvimento local):
   ```properties
   MYSQL_ROOT_PASSWORD=root
   API_SECURITY_TOKEN_SECRET=seu_segredo_jwt_aqui
   ```

> **Nota:** Os scripts de automação (`run.sh` e `test.sh`) são configurados para falhar caso o arquivo `.env` não exista ou as variáveis não sejam carregadas, prevenindo execução insegura.

---

## ⚙️ Automação e Scripts (CLI)

Para facilitar o dia-a-dia, criamos um CLI robusto em Bash.

### 1. Gerenciador Principal (`automation/run.sh`)
Interface interativa para construir e rodar o projeto.
- **Uso:** `./automation/run.sh`
- **Funcionalidades:**
  - Seleção de Profiles (Dev, Test, Prod).
  - Clean Build (limpa volumes e reconstrói).
  - Quick Start (apenas sobe os containers).
  - Carregamento automático do `.env`.

### 2. Executor de Testes (`automation/test.sh`)
Roda toda a suíte de testes em containers efêmeros, garantindo que o ambiente de teste seja idêntico ao de produção.
- **Backend:** Executa `mvn test` em container isolado.
- **Frontend:** Executa `npm run test:unit` em container Node.js.
- **Integração:** Usa a flag `--env-file` para garantir que os testes tenham acesso às configurações corretas.

---

## 🚀 Perfis de Ambiente (Spring Profiles)

O Backend suporta múltiplos perfis de configuração, controlados via `SPRING_PROFILES_ACTIVE`:

| Profile | Arquivo Config | Comportamento Swagger | Logs SQL | Uso |
| :--- | :--- | :--- | :--- | :--- |
| **dev** | `application-dev.yml` | **Habilitado** | Ativos | Desenvolvimento Local |
| **prod** | `application-prod.yml` | **Desabilitado** | Inativos | Produção / Deploy |
| **test** | `application-test.yml` | Desabilitado | Mínimos | Testes Automatizados |

### Segurança em Produção
No perfil `prod`:
- O Swagger (`/swagger-ui.html`) retorna 404/403.
- Detalhes de erro do `Actuator` são ocultados.
- Validação estrita do schema do banco (`validate`).

---

## 🌐 Build Otimizado (Frontend)

O Frontend utiliza **Vite** com estratégia de **Manual Chunking** configurada no `vite.config.ts`.
- **Resultado:** Separação das bibliotecas `vue` e `vuetify` em arquivos `.js` distintos (`vendor-vue`, `vendor-vuetify`).
- **Benefício:** Melhor cacheamento pelo navegador e carregamento inicial mais rápido.
