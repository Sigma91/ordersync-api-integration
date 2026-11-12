package dev.ordersync.integration;

public interface ProviderClient {
    ProviderResponse submit(ProviderOrder order);
}
