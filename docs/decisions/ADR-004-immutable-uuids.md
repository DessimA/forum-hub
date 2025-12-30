# ADR 004: Random Immutable UUIDs for Entity Identification

## Status
Accepted

## Context
Initial implementation used sequential `Long` IDs (AUTO_INCREMENT). While efficient for storage, it caused several issues:
1.  **Insecure Direct Object References (IDOR):** Sequential IDs are predictable, allowing users to guess other resources' URLs.
2.  **Recovery Failure:** When an entity (like a Topic) was deleted and later restored from audit logs, it was assigned a new ID by the database. This broke existing links in audit logs for child entities (like Answers) which referenced the "old" ID.
3.  **Cross-Environment Consistency:** It was difficult to maintain consistent IDs across dev, test, and prod environments.

## Decision
We switched all entity identifiers from `Long` to random, immutable `UUID` (java.util.UUID).
- **Backend:** Entities use `BINARY(16)` in MySQL for storage and `UUID` type in JPA.
- **Generation:** IDs are generated in Java via `@PrePersist` hooks if not provided.
- **Immutability:** Once an ID is assigned, it never changes. Even if an entity is deleted and restored, it returns with its **original random UUID**.
- **Audit Logs:** References to entities in logs now use the string representation of the UUID.

## Consequences
- **Pros:** 100% robust data recovery system, non-predictable URLs, improved security against IDOR, simplified integration testing.
- **Cons:** Slightly increased database storage size (16 bytes vs 8 bytes), more complex SQL queries (need `UUID_TO_BIN` for manual inserts).
