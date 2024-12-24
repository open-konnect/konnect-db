package org.konnect.cluster;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClusterNode {

    private final String nodeId;
    private final String host;
    private final int port;
    private final List<String> clusterNodes; // List of active nodes
    private final ConsistentHashing hashRing;

    public ClusterNode(String nodeId, String host, int port) {
        this.nodeId = nodeId;
        this.host = host;
        this.port = port;
        this.clusterNodes = new CopyOnWriteArrayList<>();
        this.hashRing = new ConsistentHashing(3); // 3 virtual nodes per node
        addNodeToRing(nodeId); // Add self to hash ring
    }

    // Add a new node to the cluster
    public synchronized void addNode(String node) {
        if (!clusterNodes.contains(node)) {
            clusterNodes.add(node);
            hashRing.addNode(node);
            broadcastClusterUpdate();
        }
    }

    // Remove a node from the cluster
    public synchronized void removeNode(String node) {
        if (clusterNodes.contains(node)) {
            clusterNodes.remove(node);
            hashRing.removeNode(node);
            broadcastClusterUpdate();
        }
    }

    // Recalculate the hash ring when the cluster changes
    private void addNodeToRing(String node) {
        hashRing.addNode(node);
    }

    // Broadcast cluster updates to other nodes
    private void broadcastClusterUpdate() {
        for (String node : clusterNodes) {
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

    // Get the list of active nodes
    public List<String> getClusterNodes() {
        return clusterNodes;
    }


}
