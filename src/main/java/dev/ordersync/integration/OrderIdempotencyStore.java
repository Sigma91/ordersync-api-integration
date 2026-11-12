package dev.ordersync.integration;

import dev.ordersync.api.OrderReceipt;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Demo-only, process-local idempotency store.
 * Serializes submissions to prevent duplicate concurrent calls within one JVM.
 * Not durable, distributed, or appropriate for production use.
 */
@Component
public class OrderIdempotencyStore {
    private final Map<String, Entry> completed = new HashMap<>();

    public synchronized OrderReceipt execute(String orderId, ProviderOrder mappedOrder,
                                             Supplier<OrderReceipt> submit) {
        Entry previous = completed.get(orderId);
        if (previous != null) {
            if (!previous.order().equals(mappedOrder)) {
                throw new IdempotencyConflictException();
            }
            return previous.receipt();
        }
        // Failed submissions are intentionally not cached; retry is possible.
        OrderReceipt receipt = submit.get();
        completed.put(orderId, new Entry(mappedOrder, receipt));
        return receipt;
    }

    private record Entry(ProviderOrder order, OrderReceipt receipt) {}
}
