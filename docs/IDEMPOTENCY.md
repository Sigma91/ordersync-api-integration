# Demo idempotency behavior

The **orderId** is used as the idempotency key. Within one running application instance:

- First valid request: mapped and submitted to the in-process mock provider.
- Same orderId and equivalent mapped order: returns the original receipt; provider is not called again.
- Same orderId with different mapped data: HTTP 409 with `ORDER_REFERENCE_CONFLICT`.
- Provider exception: not cached; a later request can retry.
- Concurrent requests in the same JVM are serialized to avoid duplicate mock submissions.

The key is the trimmed order ID. Equivalence uses `ProviderOrder` record equality,
including `BigDecimal` scale: amounts such as `42.5` and `42.50` compare differently
and cause a conflict for the same key. Failed submissions can be retried by the
caller; the application does not perform automatic retries.

## Critical limitations

This is an educational in-memory implementation, **not production-safe**:
- Restart clears all keys; multiple application replicas have independent state.
- Storage grows without bounds.
- The JVM lock serializes all orders, reducing throughput.
- If a real provider accepts a request but the client times out, retrying may duplicate the shipment.
- No durable state, distributed locking, provider-side idempotency or recovery mechanism.

Production follow-up: PostgreSQL uniqueness + transactional state machine, provider idempotency key, reconciliation of ambiguous outcomes, retention policy, and targeted concurrency tests.
