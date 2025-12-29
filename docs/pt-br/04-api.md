# 04 - Documentação da API

## 🔐 Segurança
Todas as rotas (exceto login/cadastro) exigem um Token JWT no Header:
`Authorization: Bearer <seu_token>`

## 📍 Endpoints Principais

### Autenticação
- `POST /api/v1/auth/login`: Retorna o token JWT.
- `POST /api/v1/auth/register`: Cadastro de novos usuários.

### Tópicos
- `GET /api/v1/topics`: Lista com paginação.
- `POST /api/v1/topics`: Criação (Requer ROLE_USER).
- `DELETE /api/v1/topics/{id}`: Exclusão (Requer justificativa se não for o autor).

### Usuários e Moderação
- `POST /api/v1/users/{id}/block`: Bloqueio de usuário (Admin/Moderator).
- `PATCH /api/v1/users/{id}/role`: Alteração de perfil (Admin).

## 📖 Swagger (OpenAPI)
A documentação interativa completa (onde você pode testar os endpoints) está disponível em:
`http://localhost:8080/swagger-ui.html`
