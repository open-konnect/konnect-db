package org.konnect.cluster;

import lombok.Getter;
import org.konnect.utils.NodeIdUtils;

@Getter
public class Node {
    private final int index;
    private final String name;
    private final String clusterEndpoint;
    private final String apiEndpoint;

    public Node(int idx) {
        this.index = idx;
        this.name = NodeIdUtils.nodeNameFromIndex(idx);
        this.clusterEndpoint = NodeIdUtils.nodeEndpointFromIndex(idx, ClusterConstants.NODE_PORT);
        this.apiEndpoint = NodeIdUtils.nodeEndpointFromIndex(idx, ClusterConstants.API_PORT);
    }

    public Node(String name) {
        this(NodeIdUtils.getNodeIndex(name));
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Node) {
            return ((Node) other).index == this.index;
        }
        return false;
    }
}
