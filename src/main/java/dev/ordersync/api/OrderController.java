package dev.ordersync.api;

import dev.ordersync.integration.OrderIntegrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderIntegrationService integrationService;

    public OrderController(OrderIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderReceipt receive(@Valid @RequestBody CreateOrderRequest request) {
        return integrationService.submit(request);
    }
}
