package org.konnect.beans;

import org.konnect.api.KeyValueController;
import org.konnect.core.AbstractKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerBeans {

    @Bean
    public KeyValueController keyValueController(
            @Qualifier("fileKeyValueStore") AbstractKeyValueStore keyValueStore) {
        return new KeyValueController(keyValueStore);
    }
}
