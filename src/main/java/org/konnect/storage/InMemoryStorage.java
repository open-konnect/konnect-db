package org.konnect.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class InMemoryStorage implements BaseStorage {

    private final ConcurrentSkipListMap<String, String> localMap;

    public InMemoryStorage() {
        this.localMap = new ConcurrentSkipListMap<>();
    }

    @Override
    public String read(String key) {
        return localMap.get(key);
    }

    @Override
    public void write(String key, String value) {
        localMap.put(key, value);
    }

    @Override
    public void delete(String key) {
        localMap.remove(key);
    }

    @Override
    public List<String> scan(String startKey, String endKey) {
        return new ArrayList<>(localMap.subMap(startKey, true, endKey, true).keySet());
    }

    @Override
    public void compaction() {
        // Do nothing
    }

    public void close() {
        localMap.clear();
    }
}
