package org.konnect.storage;

import java.util.List;

public interface BaseStorage {

    String read(String namespace, String key);

    void write(String namespace, String key, String value);

    void delete(String namespace, String key);

    List<String> scan(String namespace, String startKey, String endKey);

    void compaction();

    void close();
}
