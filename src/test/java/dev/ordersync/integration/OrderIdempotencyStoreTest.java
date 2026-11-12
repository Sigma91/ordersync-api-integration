package dev.ordersync.integration;

import dev.ordersync.api.OrderReceipt;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class OrderIdempotencyStoreTest {
    private final OrderIdempotencyStore store = new OrderIdempotencyStore();
    private final ProviderOrder order = new ProviderOrder("ORD-1", "Jane", new BigDecimal("42.50"), "EUR");

    @Test
    void repeatedIdenticalOrderReturnsOriginalReceipt() {
        AtomicInteger calls = new AtomicInteger();
        var first = store.execute("ORD-1", order, () -> {
            calls.incrementAndGet();
            return new OrderReceipt("MOCK-ORD-1", "SUBMITTED_TO_MOCK");
        });
        var second = store.execute("ORD-1", order, () -> {
            calls.incrementAndGet();
            return new OrderReceipt("unexpected", "SUBMITTED_TO_MOCK");
        });
        assertEquals(first, second);
        assertEquals(1, calls.get());
    }

    @Test
    void sameReferenceWithDifferentPayloadConflicts() {
        store.execute("ORD-1", order, () -> new OrderReceipt("MOCK-ORD-1", "SUBMITTED_TO_MOCK"));
        var changed = new ProviderOrder("ORD-1", "Other", new BigDecimal("42.50"), "EUR");
        assertThrows(IdempotencyConflictException.class, () ->
                store.execute("ORD-1", changed, () -> new OrderReceipt("X", "SUBMITTED_TO_MOCK")));
    }

    @Test
    void failedSubmissionCanBeRetried() {
        assertThrows(IllegalStateException.class, () ->
                store.execute("ORD-1", order, () -> { throw new IllegalStateException("failure"); }));
        var result = store.execute("ORD-1", order, () -> new OrderReceipt("MOCK-ORD-1", "SUBMITTED_TO_MOCK"));
        assertEquals("MOCK-ORD-1", result.reference());
    }

    @Test
    void concurrentDuplicateSubmissionsOnlyExecuteOnce() throws Exception {
        var pool = Executors.newFixedThreadPool(8);
        var ready = new CountDownLatch(8);
        var start = new CountDownLatch(1);
        var calls = new AtomicInteger();
        try {
            var tasks = new java.util.ArrayList<Future<OrderReceipt>>();
            for (int i = 0; i < 8; i++) {
                tasks.add(pool.submit(() -> {
                    ready.countDown();
                    start.await();
                    return store.execute("ORD-1", order, () -> {
                        calls.incrementAndGet();
                        return new OrderReceipt("MOCK-ORD-1", "SUBMITTED_TO_MOCK");
                    });
                }));
            }
            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            for (var task : tasks) assertEquals("MOCK-ORD-1", task.get(5, TimeUnit.SECONDS).reference());
            assertEquals(1, calls.get());
        } finally {
            pool.shutdownNow();
        }
    }
}
