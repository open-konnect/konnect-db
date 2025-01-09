package org.konnect.cluster;

public class ClusterConstants {

    private static final String NODE_ID_KEY = "NODE_ID";
    private static final String NODE_IP_KEY = "NODE_IP";
    private static final String NODE_PORT_KEY = "NODE_PORT";
    private static final String SEED_NODE_KEY = "SEED_NODE";
    private static final String MAX_REPLICA_KEY = "MAX_REPLICA";

    public static final String SELF_NODE_ID = System.getenv(NODE_ID_KEY);
    public static final String SELF_NODE_IP = System.getenv(NODE_IP_KEY);
    public static final int NODE_PORT = Integer.parseInt(System.getenv(NODE_PORT_KEY));
    public static final int API_PORT = 8080;
    public static final String SEED_NODE = System.getenv(SEED_NODE_KEY);
    public static final int MAX_REPLICA = Integer.parseInt(System.getenv(MAX_REPLICA_KEY));
}
