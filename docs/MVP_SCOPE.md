# OrderSync - MVP Scope

## Goal

Demonstrate Java 25 / Spring Boot order integration between a fictional store
and a fictional logistics provider using synthetic data and an in-process mock.

## Implemented

- Receive and validate JSON orders at `POST /api/orders`.
- Transform the inbound model into a distinct provider request model.
- Call the local mock and return a synthetic receipt with HTTP 202.
- Reuse completed equivalent mapped orders and reject conflicting duplicates.
- Allow retry after provider failure; serialize concurrent submissions in one JVM.
- Map idempotency conflicts to HTTP 409 and provider `IllegalStateException`
  to HTTP 502 without returning the exception message.
- Provide MVC slice and unit tests, Maven Wrapper setup, and Java 25 CI.

See the [README](../README.md) for setup, request/response examples, and the
actual test coverage; see [architecture](ARCHITECTURE.md) for validation and mapping.

## Out of scope

- Real carrier credentials, customer data, or third-party production systems.
- Real HTTP provider calls, timeout handling, and failure simulation in the built-in mock.
- Durable retries/backoff, persistence, authentication, and production observability.
- Distributed idempotency, key expiry, and reconciliation of ambiguous provider outcomes.
- Kafka, Kubernetes, cloud deployment, and payment processing.

The mock always succeeds. Failure behavior is exercised with test doubles.
The implementation is educational and is not production-ready; the
[idempotency limitations](IDEMPOTENCY.md#critical-limitations) explain storage,
concurrency, restart, and retry risks.

## Follow-up

Potential separate increments include a real HTTP client with explicit timeouts,
bounded retries/backoff, and PostgreSQL-backed status and idempotency tracking.
These are future work, not current capabilities.
