# 03 - Infraestrutura e Docker

## 🐳 Containerização
O projeto é 100% containerizado para garantir paridade entre ambientes. Utilizamos o `docker-compose` para orquestrar os serviços principais:

| Serviço | Imagem Base | Porta | Descrição |
| :--- | :--- | :--- | :--- |
| **MySQL** | `mysql:8.4` | 3306 | Persistência de dados (Volumes em `./infra/mysql_data`). |
| **Backend** | `eclipse-temurin:17` | 8080 | API Spring Boot (Multi-stage build). |
| **Frontend** | `nginx:stable-alpine` | 80/3000 | SPA Vue.js servida por Nginx. |

## ⚙️ Scripts de Automação
Facilitamos a execução através de scripts bash:
- `./automation/run.sh`: Menu interativo para gerenciar perfis e serviços.
- `./automation/test.sh`: Suite de testes completa (Backend + Frontend) via Docker.

### Perfis de Execução (Profiles)
- **dev:** Ambiente de desenvolvimento com banco MySQL.
- **test:** Ambiente isolado para testes usando H2 (MySQL Mode) e JUnit 5.
- **prod:** Configuração otimizada para produção.

## 🗄️ Gerenciamento de Banco (Flyway)
O sistema utiliza Flyway para controle de versão do esquema. As migrações foram estabilizadas para serem compatíveis tanto com **MySQL 8.4** quanto com **H2 (em modo compatibilidade)**:
- **V1:** Tabelas fundamentais com UUID (`BINARY(16)`).
- **V2:** Carga inicial usando literais hexadecimais padrão (`X'...'`) para compatibilidade cruzada.
- **V3/V4:** Extensões de moderacão e auditoria.
- **V5:** Índices de performance (`idx_topics_author`, `idx_audit_entity`, etc).

## 💓 Saúde e Monitoramento
- **Actuator:** `http://localhost:8080/actuator/health` fornece o status em tempo real.
- **Swagger UI:** `http://localhost:8080/swagger-ui.html` para testes interativos dos endpoints.