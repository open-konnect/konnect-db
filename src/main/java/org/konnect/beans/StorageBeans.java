package org.konnect.beans;

import org.konnect.core.AbstractKeyValueStore;
import org.konnect.core.FileKeyValueStore;
import org.konnect.core.InMemoryKeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageBeans {

    @Value("${STORAGE_PATH}")
    private String basePath;

    @Bean("inMemoryKeyValueStore")
    public AbstractKeyValueStore inMemoryKeyValueStore() {
        return new InMemoryKeyValueStore();
    }

    @Bean("fileKeyValueStore")
    public AbstractKeyValueStore fileKeyValueStore() {
        FileKeyValueStore keyValueStore = new FileKeyValueStore(basePath);
        keyValueStore.initialize();
        return keyValueStore;
    }
}
