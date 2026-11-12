package dev.ordersync.integration;

import dev.ordersync.api.CreateOrderRequest;
import dev.ordersync.api.OrderReceipt;
import org.springframework.stereotype.Service;

@Service
public class OrderIntegrationService {
    private final OrderMapper mapper;
    private final ProviderClient provider;

    public OrderIntegrationService(OrderMapper mapper, ProviderClient provider) {
        this.mapper = mapper;
        this.provider = provider;
    }

    public OrderReceipt submit(CreateOrderRequest order) {
        ProviderResponse response = provider.submit(mapper.toProviderOrder(order));
        return new OrderReceipt(response.providerReference(), "SUBMITTED_TO_MOCK");
    }
}
