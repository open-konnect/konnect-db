package org.konnect.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

import static org.konnect.cluster.ClusterConstants.*;

@SpringBootApplication
@ComponentScan("org.konnect.beans")
public class KonnectDBApplication {

    private static Logger log = LoggerFactory.getLogger(KonnectDBApplication.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(KonnectDBApplication.class, args);
        //startClusterNode();
    }

    private static void startClusterNode() throws IOException, InterruptedException {
        log.info("Node Id {} and Ip {} port {} and seed {} found", SELF_NODE_ID, SELF_NODE_IP, NODE_PORT, SEED_NODE);
        // ClusterNode selfNode = new ClusterNode(nodeId, nodeIp, nodePort);

        //SelfNode.INSTANCE.bootstrap();
        //new ClusterServer().start();

        // Join the cluster using a seed node
        // ClusterUtils.tryJoinCluster(selfNode, seedNode);

        // Start periodic peer sync
        //ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        //executor.scheduleAtFixedRate(new PeerSyncTask(), 20, 20, TimeUnit.SECONDS);
    }
}
