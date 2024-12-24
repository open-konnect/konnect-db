package org.konnect.core;

import org.konnect.storage.BaseStorage;
import org.konnect.storage.InMemoryStorage;

import java.util.Map;

public class InMemoryKeyValueStore extends AbstractKeyValueStore {

    public InMemoryKeyValueStore() {
        super();
    }

    @Override
    protected BaseStorage provideNamespaceStorage(String namespace) {
        return new InMemoryStorage();
    }

    @Override
    protected Map<String, BaseStorage> loadData() {
        return Map.of();
    }
}
