package org.konnect.cluster;

import lombok.Getter;
import org.konnect.utils.NodeIdUtils;
import org.konnect.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClusterNode {

    @Getter private final String nodeId;
    @Getter private final int nodeIdx;
    @Getter private final String host;
    @Getter private final int port;
    @Getter private final List<String> peerNodes; // List of active nodes
    private final ConsistentHashingOld hashRing;

    public ClusterNode(String nodeId, String host, int port) {
        this.nodeId = nodeId;
        this.nodeIdx = NodeIdUtils.getNodeIndex(nodeId);
        this.host = host;
        this.port = port;
        this.peerNodes = new CopyOnWriteArrayList<>();
        this.hashRing = new ConsistentHashingOld(3); // 3 virtual nodes per node
        addNodeToRing(nodeId); // Add self to hash ring
    }

    // Add a new node to the cluster
    public synchronized void addNode(String node) {
        if (StringUtils.isBlank(node)) {
            return;
        }
        node = node.trim();
        if (!nodeId.equals(node) && !peerNodes.contains(node)) {
            peerNodes.add(node);
            hashRing.addNode(node);
            broadcastClusterUpdate();
        }
    }

    // Remove a node from the cluster
    public synchronized void removeNode(String node) {
        if (StringUtils.isBlank(node)) {
            return;
        }
        node = node.trim();
        if (peerNodes.contains(node)) {
            peerNodes.remove(node);
            hashRing.removeNode(node);
            broadcastClusterUpdate();
        }
    }

    public List<String> getAllNodes() {
        List<String> allNodes = new ArrayList<>();
        allNodes.add(nodeId);
        allNodes.addAll(peerNodes);
        return allNodes;
    }

    // Recalculate the hash ring when the cluster changes
    private void addNodeToRing(String node) {
        hashRing.addNode(node);
    }

    // Broadcast cluster updates to other nodes
    private void broadcastClusterUpdate() {
        for (String node : peerNodes) {
            if (!node.equals(nodeId)) {
                // Call an API on the node to sync cluster topology
                // Example: httpClient.post(node + "/update-cluster", clusterNodes);
            }
        }
    }

    // Get the node responsible for a key and namespace
    public String getNodeForKey(String namespace, String key) {
        return hashRing.getNode(namespace, key);
    }
}
