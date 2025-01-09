package org.konnect.core;

import org.konnect.app.KonnectDBApplication;
import org.konnect.cluster.RoutingInfo;
import org.konnect.cluster.SelfNode;
import org.konnect.storage.BaseStorage;
import org.konnect.storage.ProxyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractKeyValueStore {

    private static Logger log = LoggerFactory.getLogger(AbstractKeyValueStore.class);


    protected abstract BaseStorage provideStorage(String namespace, String key);
    protected abstract BaseStorage createStorage(String namespace, String key);

    protected abstract Map<String, BaseStorage> loadData();

    private final ConcurrentHashMap<String, BaseStorage> namespaceMap;

    private final ProxyStoreProvider proxyStoreProvider;

    private final SelfNode selfNode;

    public AbstractKeyValueStore(SelfNode selfNode) {
        this.selfNode = selfNode;
        this.namespaceMap = new ConcurrentHashMap<>();
        this.proxyStoreProvider = new ProxyStoreProvider();
    }

    public void initialize() {
        this.namespaceMap.putAll(loadData());
    }

    public String get(String namespace, String key) {
        BaseStorage storage = fetchStorage(namespace, key, false);
        return storage != null ? storage.read(namespace, key) : null;
    }

    public void put(String namespace, String key, String value) {
        BaseStorage storage = fetchStorage(namespace, key, true);
        storage.write(namespace, key, value);
    }

    public void delete(String namespace, String key) {
        BaseStorage storage = fetchStorage(namespace, key, false);
        if (storage != null) {
            storage.delete(namespace, key);
        }
    }

    public List<String> scan(String namespace, String startKey, String endKey) {
        BaseStorage storage = fetchStorage(namespace, startKey, false);
        if (storage != null) {
            return storage.scan(namespace, startKey, endKey);
        }
        return null;
    }

    private BaseStorage fetchStorage(String namespace, String key, boolean createIfNull) {
        RoutingInfo routingInfo = selfNode != null
                ? selfNode.getRoutingInfo(namespace, key)
                : RoutingInfo.builder().isPresentInSelf(true).build();
        BaseStorage storage = provideStorage(namespace, key);
        if (routingInfo.isPresentInSelf()) {
            log.info("Namespace {} and key {} found in self node", namespace, key);
            if (storage == null && createIfNull) {
                storage = createStorage(namespace, key);
            }
        } else {
            log.info("Namespace {} and key {} NOT found in self node", namespace, key);
            storage = proxyStoreProvider.provideProxy(routingInfo.getPeerNodes().getFirst());
        }
        return storage;
    }
}
