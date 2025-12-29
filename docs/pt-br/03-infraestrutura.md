# Infraestrutura e Ecossistema de Automação

## Conteinerização com Docker
O FórumHub foi projetado para ser agnóstico de ambiente ("run anywhere").

### Backend Dockerfile
Utiliza **Multi-stage Build** para otimização extrema:
1. **Stage 1 (Build):** Usa a imagem do Maven para compilar o código e rodar os testes.
2. **Stage 2 (Final):** Usa uma imagem JRE leve (Eclipse Temurin) para rodar o `.jar` gerado, reduzindo o tamanho da imagem de ~600MB para ~200MB.

### Database (MySQL 8.0)
Configurado para persistência através de volumes. As migrations do Flyway garantem que o esquema do banco seja recriado fielmente em qualquer novo container.

## Sistema de Automação (Shell Scripts)
O script `automation/run.sh` é o coração da experiência do desenvolvedor.

### Funcionalidades do Script:
- **Verificação de Requisitos:** Checa se o Docker e o Docker Compose estão instalados antes de iniciar.
- **Gerenciamento de Profiles:**
  - `dev`: Ativa DevTools e logs detalhados.
  - `test`: Sobe containers efêmeros apenas para a execução da suíte de testes.
  - `prod`: Configurações de segurança estritas e Nginx para o frontend.
- **Orquestração de Espera:** O script utiliza `healthchecks` para garantir que o MySQL esteja aceitando conexões antes de tentar iniciar o Backend, evitando falhas de inicialização.

## Fluxo de CI/CD Simulado
Embora o projeto rode localmente, a estrutura de pastas e scripts está pronta para integração com GitHub Actions ou Jenkins:
1. `cleanup.ps1` limpa o workspace.
2. `test.sh` executa a validação total.
3. `docker-compose.yml` faz o deploy.