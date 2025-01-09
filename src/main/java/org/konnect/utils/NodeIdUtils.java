package org.konnect.utils;

import org.konnect.cluster.Node;

import java.net.MalformedURLException;
import java.net.URL;

public class NodeIdUtils {

    /**
     * Nodes are deployed via kubernetes and follow naming convention of konnect-db-0, konnect-db-1 etc.
     * Cluster APIs are sent to host at konnect-db-0.konnect-db:9090 where 9090 is the cluster server port
     * cluster server port coming as environment variable NODE_PORT
     */

    private static final String NODE_PREFIX = "konnect-db-";
    private static final String NODE_SUFFIX = ".konnect-db";
    private static final String NODE_ENDPOINT_IDX = "http://konnect-db-%s.konnect-db:%s";
    private static final String NODE_ENDPOINT_NAME = "http://%s.konnect-db:%s";

    public static int getNodeIndex(String nodeId) {
        if (StringUtils.isBlank(nodeId)) {
            return -1;
        }
        String[] parts = nodeId.split(NODE_PREFIX);
        if (parts.length < 2) {
            return -1;
        }
        try {
            String[] nodeParts = parts[1].split(NODE_SUFFIX);
            return Integer.parseInt(nodeParts[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String nodeNameFromIndex(int nodeIdx) {
        return NODE_PREFIX + nodeIdx;
    }
    public static String nodeEndpointFromIndex(int nodeIdx, int port) {
        return String.format(NODE_ENDPOINT_IDX, nodeIdx, port);
    }

    public static String nodeEndpointFromName(String nodeName, int port) {
        return String.format(NODE_ENDPOINT_NAME, nodeName, port);
    }
}
