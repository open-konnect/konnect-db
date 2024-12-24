package org.konnect.core;

import org.konnect.storage.BaseStorage;
import org.konnect.storage.FileStore;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileKeyValueStore extends AbstractKeyValueStore {

    private final String basePath;
    private final Map<String, FileStore> namespaceFiles; // Tenant-specific data

    // Constructor
    public FileKeyValueStore(String basePath) {
        this.basePath = basePath;
        this.namespaceFiles = new ConcurrentHashMap<>();
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

    @Override
    protected BaseStorage provideNamespaceStorage(String namespace) {
        return new FileStore(basePath + "/" + namespace + ".db");
    }

}
