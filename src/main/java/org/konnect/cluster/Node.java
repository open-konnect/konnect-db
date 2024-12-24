package org.konnect.cluster;

import java.util.HashSet;
import java.util.Set;

public class Node {
    private final String id; // Unique identifier for the node
    private final String host;
    private final int port;
    private final Set<String> knownPeers; // List of known peer nodes

    public Node(String id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.knownPeers = new HashSet<>();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Set<String> getKnownPeers() {
        return knownPeers;
    }

    // Add a new peer
    public void addPeer(String peer) {
        knownPeers.add(peer);
    }

    // Remove a peer
    public void removePeer(String peer) {
        knownPeers.remove(peer);
    }
}
