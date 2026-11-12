package dev.ordersync.integration;

import dev.ordersync.api.CreateOrderRequest;
import dev.ordersync.api.OrderReceipt;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderIntegrationServiceTest {
    @Test
    void mapsAndSubmitsToProvider() {
        ProviderClient provider = mock(ProviderClient.class);
        when(provider.submit(any())).thenReturn(new ProviderResponse("P-123"));
        var service = new OrderIntegrationService(new OrderMapper(), provider);
        OrderReceipt receipt = service.submit(new CreateOrderRequest(" ORD-1 ", " Jane ", new BigDecimal("42.50"), "eur"));
        assertEquals("P-123", receipt.reference());
        assertEquals("SUBMITTED_TO_MOCK", receipt.status());
        verify(provider).submit(new ProviderOrder("ORD-1", "Jane", new BigDecimal("42.50"), "EUR"));
    }

    @Test
    void propagatesProviderFailure() {
        ProviderClient provider = mock(ProviderClient.class);
        when(provider.submit(any())).thenThrow(new IllegalStateException("Provider unavailable"));
        var service = new OrderIntegrationService(new OrderMapper(), provider);
        assertThrows(IllegalStateException.class, () -> service.submit(
                new CreateOrderRequest("ORD-1", "Jane", new BigDecimal("42.50"), "EUR")));
    }
}
