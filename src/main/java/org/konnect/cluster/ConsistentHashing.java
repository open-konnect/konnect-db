package org.konnect.cluster;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConsistentHashing {

    private static final int DEFAULT_VIRTUAL_NODE_COUNT = 10;
    private static final int DEFAULT_REPLICATION_FACTOR = 1;

    // Each physical node is represented by multiple virtual nodes for better load distribution.
    private final int virtualNodeCount;
    private final int replicationFactor;
    private final SortedMap<Long, String> ring;
    private final Map<String, String> nodesMap;

    public ConsistentHashing(int virtualNodeCount, int replicationFactor) {
        this.virtualNodeCount = virtualNodeCount;
        this.replicationFactor = replicationFactor;
        this.ring = new ConcurrentSkipListMap<>();
        this.nodesMap = new ConcurrentHashMap<>();
    }

    public ConsistentHashing() {
        this(DEFAULT_VIRTUAL_NODE_COUNT, DEFAULT_REPLICATION_FACTOR);
    }

    // Add a node to the hash ring
    public void addNode(String node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            long hash = hash(node + "##" + i);
            ring.put(hash, node);
        }
    }

    // Remove a node from the hash ring
    public void removeNode(String node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            long hash = hash(node + "##" + i);
            ring.remove(hash);
        }
    }

    // Get the list of node responsible for a given key and namespace
    public List<String> getNodesForKey(String namespace, String key) {
        String compositeKey = namespace + "::" + key;
        long hash = hash(compositeKey);

        // Find the first node clockwise from the hash
        SortedMap<Long, String> tailMap = ring.tailMap(hash);
        long primaryHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        String primaryNode = ring.get(primaryHash);
        List<String> nodes = new ArrayList<>();
        nodes.add(primaryNode);
        // find replica nodes
        if (nodes.size() < this.replicationFactor) {
            SortedMap<Long, String> tailRing = ring.tailMap(primaryHash + 1);
            addReplicaNodes(tailRing, nodes);
            addReplicaNodes(ring, nodes);
        }
        return nodes;
    }

    private void addReplicaNodes(SortedMap<Long, String> partRing, List<String> replicas) {
        for (String nodeId : partRing.values()) {
            if (replicas.size() >= this.replicationFactor) break;
            if (!replicas.contains(nodeId)) {
                replicas.add(nodeId);
            }
        }
    }

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
