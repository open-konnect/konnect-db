package org.konnect.storage;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class FileStore implements BaseStorage {

    private final String filePath;
    private final ConcurrentSkipListMap<String, Long> index; // Key to file offset
    private RandomAccessFile storageFile;

    public FileStore(String filePath) {
        this.filePath = filePath;
        this.index = new ConcurrentSkipListMap<>();

        try {
            this.storageFile = new RandomAccessFile(filePath, "rw");
            refreshIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void write(String namespace, String key, String value) {
        long offset = 0;
        try {
            offset = storageFile.length();
            String entry = key + "=" + value + "\n";
            storageFile.seek(offset);
            storageFile.write(entry.getBytes());
            index.put(key, offset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String read(String namespace, String key) {
        Long offset = index.get(key);
        if (offset == null) {
            return null; // Key not found
        }

        try {
            storageFile.seek(offset);
            String line = storageFile.readLine();
            if (line != null) {
                String[] parts = line.split("=", 2);
                if (parts[0].equals(key)) {
                    return parts[1];
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void delete(String namespace, String key) {
        index.remove(key);
    }

    @Override
    public List<String> scan(String namespace, String startKey, String endKey) {
        return new ArrayList<>(index.subMap(startKey, true, endKey, true).keySet());
    }

    @Override
    public void compaction() {
        File tempFile = new File(filePath + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            // Read latest values from index
            for (Map.Entry<String, Long> entry : index.entrySet()) {
                String key = entry.getKey();
                // Currently namespace is not saved in the file since each namespace has its own file
                String value = read(null, key);
                writer.write(key + "=" + value + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            storageFile.close();
            File originalFile = new File(filePath);
            boolean deleteOriginal = originalFile.delete();
            if (!deleteOriginal) {
                throw new IOException("Failed to delete original file : " + originalFile.getAbsolutePath());
            }

            boolean renameFile = tempFile.renameTo(originalFile);
            if (!renameFile) {
                throw new IOException("Failed to rename tmp file : " + tempFile.getAbsolutePath());
            }
            storageFile = new RandomAccessFile(filePath, "rw");
            refreshIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            storageFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Load the index from the file on startup
    private void refreshIndex() throws IOException {
        if (!index.isEmpty()) {
            index.clear();
        }
        long offset = 0;
        storageFile.seek(0);
        String line;
        while ((line = storageFile.readLine()) != null) {
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                index.put(parts[0], offset);
            }
            offset = storageFile.getFilePointer();
        }
    }
}
