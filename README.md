# OrderSync - Java API Integration Demo

OrderSync is a **Java 25 / Spring Boot 3.5.6 MVP** demonstrating how a fictional
store order is validated, mapped to a logistics provider model, and submitted
to an in-process mock provider. It includes process-local duplicate protection.

**Demo only:** use synthetic data. There is no real carrier integration,
shipment booking, database, or delivery guarantee.

## Architecture

`OrderController` validates the request; `OrderIntegrationService` maps it with
`OrderMapper`, checks `OrderIdempotencyStore`, and calls the `ProviderClient`
interface implemented by `MockProviderClient`. `ApiErrorHandler` maps conflicts
and provider failures to HTTP responses.

See [architecture and field mapping](docs/ARCHITECTURE.md) for the request flow
and [MVP scope](docs/MVP_SCOPE.md) for implemented features and exclusions.

## Run and test

Install **JDK 25** and select it through `JAVA_HOME` and your `PATH`. The Maven
Wrapper downloads Maven 3.9.9 on first use; internet access is required and a
global Maven installation is optional.

Linux/macOS:

```sh
sh ./mvnw test
sh ./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

See [local setup](docs/RUN_LOCAL.md) for JDK selection, the optional project-local
JDK helper, and PowerShell API examples. The [Java 25 upgrade notes](docs/JAVA25_UPGRADE.md)
explain dependency versions and compatibility verification.

## Submit an order

With the application running, send this request from another terminal
(POSIX shell syntax):

```sh
curl -i -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"ORD-101","customerName":"Demo Customer","amount":150.00,"currency":"EUR"}'
```

Expected status: **202 Accepted**.

```json
{"reference":"MOCK-ORD-101","status":"SUBMITTED_TO_MOCK"}
```

This receipt confirms completion of the local mock call. The mock performs no
network requests and always returns `MOCK-` followed by the mapped order ID.

| HTTP status | When it occurs |
| --- | --- |
| 202 Accepted | Successful mock submission, or replay of a previously completed equivalent mapped order |
| 400 Bad Request | Request validation fails or the JSON cannot be read |
| 409 Conflict | An existing order ID is reused with different mapped data; body: `{"error":"ORDER_REFERENCE_CONFLICT"}` |
| 502 Bad Gateway | A provider-side `IllegalStateException` reaches the error handler; body: `{"error":"UPSTREAM_FAILURE"}` |

The built-in mock has no failure or timeout simulation. Provider failure is
exercised through Mockito in service tests. See [idempotency behavior](docs/IDEMPOTENCY.md)
for duplicate, conflict, retry, and concurrency details.

## Tests

The existing suite contains **10 JUnit tests**: 3 MVC slice tests for acceptance,
validation, and malformed JSON; 3 service unit tests for mapping, provider failure
propagation, and duplicates; and 4 store unit tests for replay, conflicts, retry
after failure, and concurrent duplicates. Tests use Spring MockMvc and Mockito.
HTTP 409 and 502 error bodies are not currently asserted by the MVC tests.

GitHub Actions selects Temurin 25 and runs `mvn -B verify` on pull requests and
pushes to `main` and the Java compatibility branch.

## Limitations

This MVP is not production-ready. Idempotency is in memory, resets on restart,
is not shared across replicas, has no expiry, and serializes all submissions.
There is no authentication, durable retry, real HTTP provider client, or timeout
handling. See [critical idempotency limitations](docs/IDEMPOTENCY.md#critical-limitations)
and [MVP exclusions](docs/MVP_SCOPE.md#out-of-scope) before adapting the demo.
