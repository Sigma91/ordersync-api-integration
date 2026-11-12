# Implemented architecture

```text
Client: POST /api/orders
  |
  v
OrderController + Jakarta Bean Validation
  |
  v
OrderIntegrationService
  |
  +--> OrderMapper: CreateOrderRequest -> ProviderOrder
  |
  +--> OrderIdempotencyStore: compare/cache completed mapped orders
         |
         +--> ProviderClient -> MockProviderClient (in process)
                                  |
                                  v
                             ProviderResponse
  |
  v
OrderReceipt -> HTTP 202

ApiErrorHandler: IdempotencyConflictException -> 409
                IllegalStateException      -> 502
```

The service calls the provider inside the store's synchronized operation for
new orders. Equivalent completed orders reuse the cached receipt; conflicting
orders fail before another provider call. Failed calls are not cached.
See [idempotency behavior and limitations](IDEMPOTENCY.md).

## Input validation and mapping

Validation applies to the inbound request before mapping:

| Store field | Validation | Provider field | Mapping |
| --- | --- | --- | --- |
| `orderId` | Nonblank, at most 64 characters | `shipmentReference` | Trim |
| `customerName` | Nonblank, at most 120 characters | `recipient` | Trim |
| `amount` | Non-null, at least 0.01 | `declaredValue` | Preserve `BigDecimal` value and scale |
| `currency` | Nonblank, exactly 3 characters | `currencyCode` | Uppercase using `Locale.ROOT` |

Currency validation checks length and nonblank content; it does not validate
against an ISO currency list. Currency is not trimmed by the mapper.

## Boundaries

Inbound and provider records are separate. The controller owns HTTP acceptance,
the service owns orchestration, the mapper owns field transformation, and the
provider client owns submission. The current mock returns a synthetic reference
without network I/O. Request validation and malformed JSON use Spring MVC's
400 handling; the advice supplies the two error mappings shown above.

See the [README](../README.md#submit-an-order) for HTTP examples and responses,
and [MVP scope](MVP_SCOPE.md) for exclusions and future work.
