package org.konnect.core;

import org.konnect.storage.BaseStorage;
import org.konnect.storage.ProxyStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyStoreProvider {

    private final Map<String, ProxyStore> proxyStoreMap;

    public ProxyStoreProvider() {
        this.proxyStoreMap = new ConcurrentHashMap<>();
    }

    public BaseStorage provideProxy(String proxyName) {
        return proxyStoreMap.computeIfAbsent(proxyName, p -> new ProxyStore(proxyName));
    }
}
