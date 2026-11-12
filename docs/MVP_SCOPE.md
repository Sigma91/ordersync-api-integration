# OrderSync — MVP Scope

## Goal
Demonstrate an independently developed Java/Spring Boot integration between a fictional store and a fictional logistics provider. All data is synthetic.

## In scope
- Receive a sample order via a REST endpoint.
- Validate required fields and return clear 4xx errors.
- Map an incoming order model to a distinct provider request model.
- Send the mapped request to a local mock provider.
- Handle provider failures and timeouts without leaking secrets.
- Provide automated unit and integration tests.
- Document setup, sample curl requests and architecture.

## Out of scope for first iteration
- Real carrier credentials or third-party production systems.
- Kafka, Kubernetes, cloud deployment, payment processing.
- Persistent retries, database and authentication.

## Acceptance criteria
- A fresh clone builds and runs with documented commands.
- Tests pass locally and in CI.
- Sample order demonstrates input validation and deterministic transformation.
- Mock provider success and failure paths are tested.
- No private employer code, customer data, credentials or copied proprietary mappings.

## Follow-up
Add idempotency, bounded retry/backoff and PostgreSQL status tracking in separate reviewed increments.
