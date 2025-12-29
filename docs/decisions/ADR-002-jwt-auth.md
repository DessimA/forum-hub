# ADR 002: JWT-Based Stateless Authentication

## Status
Accepted

## Context
The application needs a secure way to authenticate users across frontend and potentially mobile clients. 
Session-based authentication (stateful) requires session management on the server side, which complicates horizontal scaling.

## Decision
We chose **JWT (JSON Web Token)** for authentication. 
Tokens are signed with a secret key (HS256) and contain user identity and roles. 
The backend remains stateless.

## Consequences
- **Pros:** Horizontally scalable (no session sync needed), mobile-friendly, standard-based.
- **Cons:** Token revocation is difficult without a blacklist (not implemented yet), payload size increases with claims, secret key management is critical.
