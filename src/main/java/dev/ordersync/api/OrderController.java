package dev.ordersync.api;

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
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderReceipt receive(@Valid @RequestBody CreateOrderRequest request) {
        // Intake-only milestone: no persistence or external delivery yet.
        return new OrderReceipt(request.orderId(), "RECEIVED");
    }
}
