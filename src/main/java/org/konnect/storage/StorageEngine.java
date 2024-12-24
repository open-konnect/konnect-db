package org.konnect.storage;

import java.util.List;

public interface StorageEngine {

    void write(String key, String value);

    String read(String key);

    void delete(String key);

    List<String> scan(String startKey, String endKey);
}
