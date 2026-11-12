# OrderSync — Java API Integration Demo

A compact, independently built **Java 17 / Spring Boot** portfolio project demonstrating how an incoming e-commerce order can be validated, transformed into a logistics-provider-specific format, and submitted to a **local mock provider**.

**Why this matters:** Businesses often need to connect systems with incompatible data models. OrderSync illustrates the integration boundary, validation, error handling, testing, and duplicate-request protection involved.

> **Demo only:** All examples are synthetic. No real carrier, shipment, database, payment system, or production credentials are involved.

## Architecture

```text
E-commerce client
       |
       | POST /api/orders (JSON)
       v
OrderController --- Bean Validation
       |
       v
OrderIntegrationService
       |
       +--> OrderMapper (store order -> provider order)
       |
       +--> OrderIdempotencyStore (single-JVM duplicate protection)
       |
       +--> ProviderClient interface
                     |
                     v
             MockProviderClient
                     |
                     v
            Synthetic provider receipt
```

## Tech stack

- Java 17 and Spring Boot 3
- Maven
- Spring Web and Jakarta Bean Validation
- JUnit 5, Mockito and Spring MVC tests
- GitHub Actions for Maven test execution

## Quick start

Requirements: **JDK 17** and **Maven 3.9+**.

```bash
mvn test
mvn spring-boot:run
```

Send a synthetic order:

```bash
curl -i -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"ORD-101","customerName":"Demo Customer","amount":150.00,"currency":"EUR"}'
```

Expected HTTP status: **202 Accepted**.

```json
{
  "reference": "MOCK-ORD-101",
  "status": "SUBMITTED_TO_MOCK"
}
```

The status confirms only that the in-process mock provider returned a receipt. **It does not mean a real shipment was booked.**

## Integration mapping

| Incoming store field | Mock provider field | Transformation |
| --- | --- | --- |
| `orderId` | `shipmentReference` | Trim whitespace |
| `customerName` | `recipient` | Trim whitespace |
| `amount` | `declaredValue` | Preserve decimal value |
| `currency` | `currencyCode` | Uppercase |

The inbound and provider-facing models are separate, allowing provider contracts to evolve independently.

## Duplicate request handling

The app uses `orderId` as a demo idempotency key within **one running JVM**.

- Same ID and equivalent mapped payload: return the first receipt without another mock-provider call.
- Same ID but different mapped payload: return **409 Conflict** with `ORDER_REFERENCE_CONFLICT`.
- Failed provider submission: do not cache the failure, allowing a retry.
- Concurrent duplicates within one instance: serialize execution to avoid duplicate mock calls.

Example conflicting second request:

```bash
curl -i -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"ORD-101","customerName":"Different Customer","amount":150.00,"currency":"EUR"}'
```

Expected: HTTP **409 Conflict**.

## Failure handling

Provider-side `IllegalStateException` is translated to **502 Bad Gateway** with a generic `UPSTREAM_FAILURE` error, avoiding exposure of internal exception messages. Invalid request bodies return **400 Bad Request**.

This is a deliberately narrow demonstration, not comprehensive production error handling.

## Testing

The repository includes controller validation tests, mapping and orchestration tests, duplicate/conflicting request tests, failure/retry tests, and an in-process concurrent duplicate-request test. GitHub Actions runs `mvn -B test` on pull requests.

## Important limitations

This project is **not production-ready**:

- Idempotency state is in memory, resets on restart, and is not shared across replicas.
- The global lock reduces throughput, and stored keys have no expiry.
- No persistent database, real HTTP provider integration, auth, or observability stack.
- A real provider may accept a request before a network timeout; retries would require provider-side idempotency and reconciliation.
- The request contains a synthetic customer name; use test data only.

## Possible next improvements

1. PostgreSQL-backed durable order and idempotency state.
2. Configurable HTTP mock server, timeouts, bounded retries and circuit-breaker policies.
3. Structured logs, correlation IDs, and an OpenAPI specification.

## Project documentation

- [MVP scope](docs/MVP_SCOPE.md)
- [Architecture notes](docs/ARCHITECTURE.md)
- [Run locally](docs/RUN_LOCAL.md)
- [Idempotency limitations](docs/IDEMPOTENCY.md)

## Portfolio note

This is an **independent demonstration project**, not a client delivery or a copy of proprietary employer code. It showcases transferable API integration skills using entirely fictional systems and synthetic data.
