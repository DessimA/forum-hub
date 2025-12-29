# Referência Técnica da API

A API do FórumHub segue a especificação OpenAPI 3.0 e utiliza os padrões mais modernos de design REST.

## Princípios de Design
- **Substantivos no Plural:** `/topics`, `/users`, `/answers`.
- **Versionamento na URL:** `/api/v1/...`
- **Paginação Obrigatória:** Todas as listagens utilizam paginação via Spring Data para evitar sobrecarga de rede.

## Detalhamento de Endpoints Críticos

### 1. Gestão de Tópicos
- **DELETE `/api/v1/topics/{id}`**
  - **Permissões:** Autor do tópico, Moderador ou Admin.
  - **Requisito:** É obrigatório passar o parâmetro `justification` na query string. Esta justificativa é salva no log de auditoria.
  - **Efeito:** Deleção em cascata de todas as respostas associadas.

### 2. Gestão de Usuários (Moderação)
- **POST `/api/v1/users/{id}/block`**
  - **Moderador:** Pode suspender usuários por no máximo 24 horas.
  - **Admin:** Pode aplicar suspensões permanentes.
  - **Validação:** Um usuário não pode bloquear a si mesmo.

### 3. Sistema de Auditoria
- **GET `/api/v1/audit-logs`**
  - **Apenas Admin.**
  - **Conteúdo:** Retorna uma lista detalhada de quem realizou ações destrutivas ou administrativas no sistema, permitindo total rastreabilidade.

## Tratamento de Erros (Respostas Padrão)
Todas as falhas retornam o seguinte objeto JSON:
```json
{
  "timestamp": "2025-12-29T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Acesso negado: você não tem permissão para realizar esta ação.",
  "path": "/api/v1/users/5/block"
}
```

## Documentação Interativa
O Swagger UI (`/swagger-ui.html`) permite que você teste os tokens JWT diretamente no navegador através da opção "Authorize" no canto superior direito.