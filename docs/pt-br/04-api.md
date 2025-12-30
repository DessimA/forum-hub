# 04 - Documentação da API

## 🔐 Segurança e Autenticação
A API utiliza **Stateless Authentication** com Tokens **JWT**. 
- Todas as rotas sob `/api/v1` (exceto `/auth/**` e `GET /courses`) exigem o header `Authorization: Bearer <token>`.
- **Padronização:** Todos os campos de entrada e saída utilizam nomes em **Inglês** (`name`, `password`, `email`, `id`).

## 📍 Endpoints em Destaque

### Autenticação (`/api/v1/auth`)
- `POST /login`: Autentica e retorna o JWT. Payload: `{ "email", "password" }`.
- `POST /register`: Cria nova conta com perfil `ROLE_USER`.

### Tópicos (`/api/v1/topics`)
- `GET /`: Listagem paginada (`Page<TopicResponseDTO>`). Parâmetros suportados: `page`, `size`, `sort`.
- `POST /`: Criação de tópico. O autor é extraído automaticamente do contexto de segurança.
- `DELETE /{id}`: Exclusão lógica com registro em auditoria. Exige justificativa se executado por moderador.

### Cursos (`/api/v1/courses`)
- `GET /`: Agora com **Paginação Mandatória** para garantir escalabilidade.
- `POST /`: Cadastro de curso (Acesso restrito: `ROLE_ADMIN`).

### Usuários e Administração (`/api/v1/users`)
- `GET /profile`: Dados do usuário logado.
- `POST /{id}/block`: Suporte a bloqueio **Permanente** ou **Temporário** (dias).
- `POST /admin/logs/{id}/restore`: Sistema único de restauração de entidades deletadas.

## 📖 Documentação Completa (Swagger)
A API é totalmente documentada com **SpringDoc / OpenAPI 3**. Acesse para ver todos os esquemas de dados e códigos de erro:
`http://localhost:8080/swagger-ui.html`