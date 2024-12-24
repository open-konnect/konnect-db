package org.konnect.core;

import org.konnect.storage.BaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractKeyValueStore {

    protected abstract BaseStorage provideNamespaceStorage(String namespace);

    protected abstract Map<String, BaseStorage> loadData();

    private final ConcurrentHashMap<String, BaseStorage> namespaceMap;

    public AbstractKeyValueStore() {
        this.namespaceMap = new ConcurrentHashMap<>();
    }

    public void initialize() {
        this.namespaceMap.putAll(loadData());
    }

    public List<String> listNamespaces() {
        return new ArrayList<>(namespaceMap.keySet());
    }

    public boolean namespaceExists(String namespace) {
        return namespaceMap.contains(namespace);
    }

    public String get(String namespace, String key) {
        BaseStorage storage = namespaceMap.get(namespace);
        if (storage != null) {
            return storage.read(key);
        }
        return null;
    }

    public void put(String namespace, String key, String value) {
        BaseStorage storage = namespaceMap.computeIfAbsent(namespace, this::provideNamespaceStorage);
        storage.write(key, value);
    }

    public void delete(String namespace, String key) {
        BaseStorage storage = namespaceMap.get(namespace);
        if (storage != null) {
            storage.delete(key);
        }
    }

    public List<String> scan(String namespace, String startKey, String endKey) {
        BaseStorage storage = namespaceMap.get(namespace);
        if (storage != null) {
            return storage.scan(startKey, endKey);
        }
        return null;
    }
}
