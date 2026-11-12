package dev.ordersync.integration;

import dev.ordersync.api.CreateOrderRequest;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public ProviderOrder toProviderOrder(CreateOrderRequest order) {
        return new ProviderOrder(order.orderId().trim(), order.customerName().trim(), order.amount(), order.currency().toUpperCase(java.util.Locale.ROOT));
    }
}
