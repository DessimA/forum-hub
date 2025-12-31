# 04 - API Documentation (REST)

The ForumHub API follows REST principles, using JSON for communication and JWT for security.

## 🔐 Authentication
- **Base URL:** `/api/v1/auth`
- **Endpoints:**
  - `POST /login`: Authenticates user and returns a token.
  - `POST /register`: Registers a new user.

## 📝 Topics
- **Base URL:** `/api/v1/topics`

### Listing with Dynamic Filters
`GET /topics`
Returns a paginated list of topics. Supports the following query parameters:

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `page` | Integer | Page number (zero-indexed). |
| `size` | Integer | Items per page (default: 10). |
| `sort` | String | Sorting (e.g., `createdAt,desc` or `title,asc`). |
| `title` | String | Partial search by title (Case-insensitive). |
| `courseId` | UUID | Filters topics from a specific course. |
| `status` | Enum | Filters by `OPEN`, `CLOSED`, or `RESOLVED`. |
| `year` | Integer | Filters by creation year. |

### Topic Lifecycle
- `POST /topics`: Creates a new topic.
- `GET /topics/{id}`: Topic details, including answers and author's blocking status.
- `PUT /topics/{id}`: Updates title, message, or **changes the course** (Author only).
- `PATCH /topics/{id}/close`: Closes the topic for new interactions.
- `PATCH /topics/{id}/reopen`: Reopens a closed topic.
- `DELETE /topics/{id}`: Logical deletion with audit backup.

## 💬 Answers and Solutions
- **Endpoints:**
  - `POST /topics/{topicId}/answers`: Adds a reply (Only for `OPEN` topics).
  - `PUT /answers/{id}`: Edits answer content (Author only).
  - `PATCH /answers/{id}/solution`: Toggles solution status (Only topic author).
  - `DELETE /answers/{id}`: Removes answer with audit backup.

## 🛡️ User Management & Moderation
- **Base URL:** `/api/v1/users`
- **Endpoints:**
  - `GET /profile`: Current user data.
  - `POST /{id}/block`: Blocks user. Accepts optional `topicId` or `answerId` for **automatic removal** of offensive content.
  - `POST /{id}/unblock`: Removes punishment (Admin/Moderator).
  - `PATCH /{id}/role`: Changes role (Admin only, with hierarchy protection).

## 📊 Auditing
- **Base URL:** `/api/v1/admin/logs`
- **Endpoints:**
  - `GET /`: Lists all deletion and change logs.
  - `POST /{id}/restore`: Restores a topic or answer using the preserved UUID.
