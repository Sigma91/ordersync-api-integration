package dev.ordersync.integration;

import java.math.BigDecimal;

public record ProviderOrder(String shipmentReference, String recipient, BigDecimal declaredValue, String currencyCode) {}
