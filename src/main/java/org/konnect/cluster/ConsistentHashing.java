package org.konnect.cluster;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashing {
    private final int virtualNodeCount; // Number of virtual nodes per physical node
    private final SortedMap<Long, String> ring; // Hash ring

    public ConsistentHashing(int virtualNodeCount) {
        this.virtualNodeCount = virtualNodeCount;
        this.ring = new TreeMap<>();
    }

    // Add a node to the hash ring
    public void addNode(String node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            long hash = hash(node + "#" + i);
            ring.put(hash, node);
        }
    }

    // Remove a node from the hash ring
    public void removeNode(String node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            long hash = hash(node + "#" + i);
            ring.remove(hash);
        }
    }

    // Get the node responsible for a given key and namespace
    public String getNode(String namespace, String key) {
        String compositeKey = namespace + ":" + key;
        long hash = hash(compositeKey);

        // Find the first node clockwise from the hash
        if (!ring.containsKey(hash)) {
            SortedMap<Long, String> tailMap = ring.tailMap(hash);
            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }
        return ring.get(hash);
    }

    // Hash function
    private long hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return ((long) (digest[0] & 0xFF) << 24) |
                    ((long) (digest[1] & 0xFF) << 16) |
                    ((long) (digest[2] & 0xFF) << 8) |
                    ((long) (digest[3] & 0xFF));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }
}
