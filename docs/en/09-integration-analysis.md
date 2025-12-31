# 09 - Backend/Frontend Integration Patterns

This document describes how the Frontend fully leverages the Backend's advanced features to deliver a superior UX.

## 🔗 Intelligent API Consumption

### 1. Dynamic Filtering (Implemented)
The Frontend consumes the `GET /topics` endpoint by passing complex filtering parameters (`courseId`, `status`, `year`, `title`).
- **Logic:** The Store centralizes these parameters, allowing pagination to work "reactively" to filters applied in the search bar.

### 2. Blocking State Synchronization (Implemented)
Topic detail responses (`TopicDetailResponseDTO`) include the updated author status (`authorIsBlocked`).
- **UX Effect:** If the author is punished, the interface displays an immediate visual indicator and offers the moderator a "Remove Punishment" button directly on the topic screen.

### 3. Moderation with Contextual Removal (Implemented)
The blocking system is integrated into the forum's data structure.
- **Workflow:** When applying a punishment through a topic, the resource ID is sent in the `BlockRequestDTO`, triggering automatic deletion and backup of offensive content in the Backend.

### 4. Reactive Lifecycle (Implemented)
The UI reacts to topic status (`OPEN`, `CLOSED`, `RESOLVED`).
- **Write Lock:** The reply field is automatically replaced by an informative alert when the topic no longer accepts interactions, preventing submission errors before the user even tries to send.