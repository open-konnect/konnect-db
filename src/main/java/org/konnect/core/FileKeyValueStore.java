package org.konnect.core;

import org.konnect.cluster.SelfNode;
import org.konnect.storage.BaseStorage;
import org.konnect.storage.FileStore;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileKeyValueStore extends AbstractKeyValueStore {

    private final String basePath;
    private final ConcurrentHashMap<String, BaseStorage> namespaceMap;
    // Constructor
    public FileKeyValueStore(String basePath, SelfNode selfNode) {
        super(selfNode);
        this.basePath = basePath;
        this.namespaceMap = new ConcurrentHashMap<>();
        ensureBasePathExists();
    }

    private void ensureBasePathExists() {
        File dir = new File(basePath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Failed to create storage directory: " + basePath);
            }
        }
    }

    @Override
    protected Map<String, BaseStorage> loadData() {
        Map<String, BaseStorage> dataMap = new HashMap<>();
        File dir = new File(basePath);
        File[] namespaceFiles = dir.listFiles((d, name) -> name.endsWith(".db"));
        if (namespaceFiles != null) {
            for (File file : namespaceFiles) {
                String namespace = file.getName().replace(".db", "");
                FileStore fileStore = new FileStore(file.getAbsolutePath());
                fileStore.compaction();
                dataMap.put(namespace, fileStore);
            }
        }
        return dataMap;
    }

    protected BaseStorage provideNamespaceStorage(String namespace) {
        return new FileStore(basePath + "/" + namespace + ".db");
    }

    @Override
    protected BaseStorage provideStorage(String namespace, String key) {
        return namespaceMap.get(namespace);
    }

    @Override
    protected BaseStorage createStorage(String namespace, String key) {
        return namespaceMap.computeIfAbsent(namespace, this::provideNamespaceStorage);
    }

}
