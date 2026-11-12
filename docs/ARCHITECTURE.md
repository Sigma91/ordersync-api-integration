# Architecture — planned

```text
Client
  |
  v
OrderController (HTTP request/response)
  |
  v
OrderIntegrationService (orchestration and error handling)
  |                 |
  v                 v
OrderMapper      ProviderClient
                      |
                      v
                Local Mock Provider
```

## Design choices
- Separate inbound and outbound DTOs: provider schemas change independently.
- Keep transformation deterministic and unit-testable.
- Keep HTTP concerns in controller and provider-specific logic in client.
- Never log secrets or full sensitive payloads.
- Use synthetic order and customer examples only.

This is a design proposal, not a claim that implementation exists yet.
