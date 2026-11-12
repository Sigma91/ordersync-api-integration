package dev.ordersync.integration;

import org.springframework.stereotype.Component;

@Component
public class MockProviderClient implements ProviderClient {
    @Override
    public ProviderResponse submit(ProviderOrder order) {
        // Synthetic provider; no network calls or external data.
        return new ProviderResponse("MOCK-" + order.shipmentReference());
    }
}
