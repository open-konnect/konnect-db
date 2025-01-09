package org.konnect.core;

import org.konnect.cluster.SelfNode;
import org.konnect.storage.BaseStorage;
import org.konnect.storage.FileStore;
import org.konnect.storage.InMemoryStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryKeyValueStore extends AbstractKeyValueStore {

    private final ConcurrentHashMap<String, BaseStorage> namespaceMap;

    public InMemoryKeyValueStore(SelfNode selfNode) {
        super(selfNode);
        this.namespaceMap = new ConcurrentHashMap<>();
    }

    protected BaseStorage provideNamespaceStorage(String namespace) {
        return new InMemoryStorage();
    }

    @Override
    protected BaseStorage provideStorage(String namespace, String key) {
        return namespaceMap.get(namespace);
    }

    @Override
    protected BaseStorage createStorage(String namespace, String key) {
        return namespaceMap.computeIfAbsent(namespace, this::provideNamespaceStorage);
    }

    @Override
    protected Map<String, BaseStorage> loadData() {
        return Map.of();
    }
}
