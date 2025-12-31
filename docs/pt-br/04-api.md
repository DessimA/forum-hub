# 04 - Documentação da API (REST)

A API do FórumHub segue os princípios REST, utilizando JSON para comunicação e JWT para segurança.

## 🔐 Autenticação
- **Base URL:** `/api/v1/auth`
- **Endpoints:**
  - `POST /login`: Autentica usuário e retorna o token.
  - `POST /register`: Registra um novo usuário.

## 📝 Tópicos
- **Base URL:** `/api/v1/topics`

### Listagem com Filtros Dinâmicos
`GET /topics`
Retorna uma página de tópicos. Suporta os seguintes parâmetros de consulta (Query Params):

| Parâmetro | Tipo | Descrição |
| :--- | :--- | :--- |
| `page` | Integer | Número da página (zero-indexed). |
| `size` | Integer | Itens por página (default: 10). |
| `sort` | String | Ordenação (ex: `createdAt,desc` ou `title,asc`). |
| `title` | String | Busca parcial por título (Case-insensitive). |
| `courseId` | UUID | Filtra tópicos de um curso específico. |
| `status` | Enum | Filtra por `OPEN`, `CLOSED` ou `RESOLVED`. |
| `year` | Integer | Filtra por ano de criação. |

### Ciclo de Vida do Tópico
- `POST /topics`: Cria um novo tópico.
- `GET /topics/{id}`: Detalhes do tópico, incluindo respostas e status de bloqueio do autor.
- `PUT /topics/{id}`: Atualiza título, mensagem ou **muda o curso** do tópico (Apenas autor).
- `PATCH /topics/{id}/close`: Fecha o tópico para novas interações.
- `PATCH /topics/{id}/reopen`: Reabre um tópico fechado.
- `DELETE /topics/{id}`: Exclusão lógica com backup na auditoria.

## 💬 Respostas e Soluções
- **Endpoints:**
  - `POST /topics/{topicId}/answers`: Adiciona resposta (Apenas tópicos `OPEN`).
  - `PUT /answers/{id}`: Edita o conteúdo da resposta (Apenas autor).
  - `PATCH /answers/{id}/solution`: Marca/Desmarca como solução (Apenas autor do tópico).
  - `DELETE /answers/{id}`: Remove resposta com backup na auditoria.

## 🛡️ Gestão de Usuários e Moderação
- **Base URL:** `/api/v1/users`
- **Endpoints:**
  - `GET /profile`: Dados do usuário logado.
  - `POST /{id}/block`: Bloqueia usuário. Aceita `topicId` ou `answerId` no corpo para **exclusão automática** do conteúdo ofensivo.
  - `POST /{id}/unblock`: Remove punição (Admin/Moderador).
  - `PATCH /{id}/role`: Altera cargo (Admin apenas, com proteção de hierarquia).

## 📊 Auditoria
- **Base URL:** `/api/v1/admin/logs`
- **Endpoints:**
  - `GET /`: Lista todos os logs de exclusão e alteração.
  - `POST /{id}/restore`: Restaura um tópico ou resposta para o banco de dados original usando o UUID preservado.
