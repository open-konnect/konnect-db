package org.konnect.app;

import org.konnect.cluster.Node;
import org.konnect.cluster.NodeServer;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        Node selfNode = getNode(args[0]);
        Node seedNode = getNode(args[1]);

        NodeServer nodeServer = new NodeServer(selfNode);
    }

    private static Node getNode(String arg) {
        String[] hostPort = arg.split(":");
        Integer port = Integer.parseInt(hostPort[1]);
        return new Node(hostPort[0], hostPort[0], port);
    }
}
