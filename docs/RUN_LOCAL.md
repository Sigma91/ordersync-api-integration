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
{"reference":"ORD-101","status":"RECEIVED"}
```

This milestone only validates and acknowledges the request. It does not persist, transform or send an order. The response is not evidence of downstream delivery.
