# 04 - Documentação da API (REST)

A API do FórumHub é construída sobre os princípios REST, utilizando JSON como formato de troca de mensagens e JWT (JSON Web Tokens) para segurança stateless.

## 📚 Swagger UI (Documentação Interativa)

A maneira mais fácil de explorar a API é através do Swagger UI, disponível em ambiente de desenvolvimento.

- **URL:** `http://localhost:8080/swagger-ui.html`
- **Recursos:**
  - Visualização de todos os endpoints.
  - Esquemas detalhados dos dados de entrada e saída (DTOs).
  - **Botão "Authorize":** Permite injetar o Token JWT para testar endpoints protegidos diretamente no navegador.

> **Segurança:** Em ambiente de produção (`profile: prod`), o Swagger é automaticamente desabilitado para proteção de dados.

---

## 🔐 Autenticação e Segurança

Todos os endpoints protegidos exigem o cabeçalho:
`Authorization: Bearer <seu_token_jwt>`

### Endpoints de Auth
- **POST** `/api/v1/auth/login`
  - **Entrada:** `LoginRequestDTO` (email, password)
  - **Saída:** `TokenResponseDTO` (token)
  - **Erros:** 401 (Credenciais Inválidas)

- **POST** `/api/v1/auth/register`
  - **Entrada:** `RegisterRequestDTO` (name, email, password)
  - **Saída:** `UserResponseDTO`
  - **Erros:** 409 (Email já cadastrado)

---

## 🌍 Internacionalização (i18n)

A API suporta múltiplos idiomas para mensagens de erro. O cliente pode solicitar o idioma desejado através do cabeçalho HTTP:

- **Header:** `Accept-Language: pt-BR` (Padrão: Inglês se não informado)
- **Resposta de Erro:**
  ```json
  {
    "timestamp": "2025-12-31T10:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Acesso negado: você não tem permissão...",
    "path": "/api/v1/users/123"
  }
  ```

---

## 📡 Endpoints Principais

### Tópicos (`/api/v1/topics`)
Gerenciamento de discussões.
- **GET** `/`: Listagem paginada. Filtros: `courseId`, `status`, `year`, `title`.
  - *Estabilidade:* A serialização de paginação é garantida via DTO (`VIA_DTO`), assegurando contratos estáveis para o frontend.
- **POST** `/`: Criação (`TopicRequestDTO`).
- **GET** `/{id}`: Detalhes completos (`TopicDetailResponseDTO`).
- **PUT** `/{id}`: Atualização (`TopicUpdateDTO`).
- **DELETE** `/{id}`: Remoção lógica.

### Respostas (`/api/v1`)
- **POST** `/topics/{topicId}/answers`: Responder a um tópico.
- **PATCH** `/answers/{id}/solution`: Marcar resposta como solução.

### Cursos (`/api/v1/courses`)
- **GET** `/`: Listagem pública de cursos.
- **POST/PUT/DELETE**: Gestão administrativa de cursos.

### Usuários (`/api/v1/users`)
- **GET** `/profile`: Dados do usuário logado.
- **POST** `/{id}/block`: Bloqueio administrativo/moderativo (`BlockRequestDTO`).

### Favoritos (`/api/v1/favorites`)
- **POST** `/topics/{topicId}`: Adiciona tópico aos favoritos do usuário.
- **DELETE** `/topics/{topicId}`: Remove tópico dos favoritos.
- **GET** `/`: Listagem paginada de tópicos favoritos.
- **GET** `/topics/{topicId}/check`: Verifica se um tópico está favoritado pelo usuário atual.

### Notificações (`/api/v1/notifications`)
- **GET** `/`: Listagem de notificações do usuário (ordenadas por data).
- **GET** `/unread-count`: Obtém a contagem de notificações não lidas.
- **PATCH** `/{id}/read`: Marca uma notificação específica como lida.
- **PATCH** `/read-all`: Marca todas as notificações como lidas.

---

## 📐 Padrões de DTO (Data Transfer Objects)

Para garantir consistência e profissionalismo, **todos os campos e anotações da API estão em Inglês**.

| DTO | Descrição | Exemplo de Campos |
| :--- | :--- | :--- |
| `RegisterRequestDTO` | Dados de cadastro | `name`, `email`, `password` |
| `TopicRequestDTO` | Novo tópico | `title`, `message`, `courseId` |
| `TopicDetailResponseDTO` | Leitura de tópico | `authorIsBlocked`, `answers` (List) |
| `NotificationResponseDTO` | Dados de notificação | `topicTitle`, `notificationType`, `isRead` |
| `UnreadCountResponseDTO` | Contador de notificações | `count` |

Esta padronização facilita a integração com clientes internacionais e segue as convenções globais de desenvolvimento Java.