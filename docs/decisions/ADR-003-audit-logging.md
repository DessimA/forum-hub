# ADR 003: Mandatory Audit Logging for Moderation

## Status
Accepted

## Context
Moderators and Admins have the power to delete content and block users. 
For transparency and compliance, these actions must be traceable and justified.

## Decision
We implemented a mandatory **Audit Logging** system. 
Any destructive action (DELETE) or punishment (BLOCK) requires a justification and is automatically logged with the performer's ID, timestamp, and content context.

## Consequences
- **Pros:** Accountability, easier dispute resolution, clear history of administrative actions.
- **Cons:** Slightly increased development effort for moderation features, database growth over time (audit logs are never deleted by standard users).
