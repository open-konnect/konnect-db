package org.konnect.storage;

import java.util.List;

public interface BaseStorage {

    String read(String key);

    void write(String key, String value);

    void delete(String key);

    List<String> scan(String startKey, String endKey);

    void compaction();

    void close();
}
