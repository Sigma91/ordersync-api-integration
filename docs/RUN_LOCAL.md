# Run OrderSync locally

Prerequisites: JDK 17 and Maven 3.9+.

```bash
mvn test
mvn spring-boot:run
```

In another terminal:

```bash
curl -i -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"ORD-101","customerName":"John","amount":150.00,"currency":"EUR"}'
```

Expected HTTP status: 202 Accepted; body:
```json
{"reference":"MOCK-ORD-101","status":"SUBMITTED_TO_MOCK"}
```

The request is mapped to a synthetic provider model and handled by an in-process mock provider. No network calls, persistence, real shipment, or delivery guarantee exists.
