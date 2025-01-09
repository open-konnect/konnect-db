package org.konnect.beans;

import org.konnect.cluster.SelfNode;
import org.konnect.core.AbstractKeyValueStore;
import org.konnect.core.FileKeyValueStore;
import org.konnect.core.InMemoryKeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class StorageBeans {

    @Value("${STORAGE_PATH}")
    private String basePath;

    @Bean("inMemoryKeyValueStore")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public AbstractKeyValueStore inMemoryKeyValueStore(SelfNode selfNode) {
        return new InMemoryKeyValueStore(selfNode);
    }

    @Bean("fileKeyValueStore")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public AbstractKeyValueStore fileKeyValueStore(SelfNode selfNode) {
        FileKeyValueStore keyValueStore = new FileKeyValueStore(basePath, selfNode);
        keyValueStore.initialize();
        return keyValueStore;
    }
}
