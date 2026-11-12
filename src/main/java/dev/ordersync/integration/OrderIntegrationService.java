package dev.ordersync.integration;

import dev.ordersync.api.CreateOrderRequest;
import dev.ordersync.api.OrderReceipt;
import org.springframework.stereotype.Service;

@Service
public class OrderIntegrationService {
    private final OrderMapper mapper;
    private final ProviderClient provider;
    private final OrderIdempotencyStore idempotency;

    public OrderIntegrationService(OrderMapper mapper, ProviderClient provider, OrderIdempotencyStore idempotency) {
        this.mapper = mapper;
        this.provider = provider;
        this.idempotency = idempotency;
    }

    public OrderReceipt submit(CreateOrderRequest order) {
        ProviderOrder mapped = mapper.toProviderOrder(order);
        return idempotency.execute(mapped.shipmentReference(), mapped, () -> {
            ProviderResponse response = provider.submit(mapped);
            return new OrderReceipt(response.providerReference(), "SUBMITTED_TO_MOCK");
        });
    }
}
