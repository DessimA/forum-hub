# 03 - Infraestrutura e Docker

## 🐳 Containerização
O projeto é 100% containerizado para garantir que ele rode da mesma forma em qualquer computador. Utilizamos o `docker-compose` para orquestrar três serviços principais:

| Serviço | Imagem Base | Porta | Descrição |
| :--- | :--- | :--- | :--- |
| **MySQL** | `mysql:8.4` | 3306 | Banco de dados relacional. |
| **Backend** | `eclipse-temurin:17` | 8080 | API Spring Boot. |
| **Frontend** | `nginx:alpine` | 80/3000 | Aplicação Vue.js. |

## ⚙️ Perfis de Execução (Profiles)
O sistema suporta múltiplos perfis através do Docker Compose:
- **prod:** Otimizado para performance, logs em nível WARN.
- **dev:** Hot-reload ativado, logs em nível DEBUG.
- **test:** Banco H2 em memória, limpeza automática de dados.

## 🗄️ Evolução do Banco (Flyway)
Não criamos tabelas manualmente. O Flyway gerencia as migrações:
- `V1`: Estrutura inicial (tabelas e chaves estrangeiras).
- `V2`: Dados iniciais (Admin padrão e cursos).
- `V5`: Índices de performance para buscas rápidas.

## 💓 Monitoramento
Utilizamos o **Spring Boot Actuator**. Você pode verificar a saúde do sistema em:
`http://localhost:8080/actuator/health`
