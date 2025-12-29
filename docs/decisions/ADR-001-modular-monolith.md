# ADR 001: Modular Monolith Architecture

## Status
Accepted

## Context
The project needs to be robust but also easy to maintain and deploy by a small team. 
A microservices architecture would introduce excessive complexity (network latency, distributed transactions, infrastructure overhead) for the current scope.

## Decision
We decided to use a **Modular Monolith** approach. 
The code is organized into modules (auth, topic, user, answer, course) that represent distinct business domains. 
Inter-module communication is currently done via standard Spring services, but boundaries are kept clear to allow future extraction into microservices if needed.

## Consequences
- **Pros:** Simpler deployment (single artifact), easier debugging, zero network latency between modules, simplified data consistency.
- **Cons:** Shared database might lead to tight coupling if not careful, scaling is all-or-nothing (vertical scaling or full monolith replication).
