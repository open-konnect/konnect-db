package org.konnect.app;

import org.konnect.cluster.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@ComponentScan("org.konnect.beans")
public class KonnectDBApplication {

    public static void main(String[] args) throws IOException {
        startClusterNode(args);
        SpringApplication.run(KonnectDBApplication.class, args);
    }

    private static Node getNode(String arg) {
        String[] hostPort = arg.split(":");
        Integer port = Integer.parseInt(hostPort[1]);
        return new Node(hostPort[0], hostPort[0], port);
    }

    private static void startClusterNode(String[] args) throws IOException {

        Node selfNode = getNode(args[0]);
        String seedNode = args[1];

        new NodeServer(selfNode).start();

        // Join the cluster using a seed node
        ClusterUtils.joinCluster(selfNode, seedNode);

        // Start periodic peer sync
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new PeerSyncTask(selfNode), 5, 5, TimeUnit.SECONDS);
    }
}
