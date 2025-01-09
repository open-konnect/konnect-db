package org.konnect.cluster;

import org.konnect.utils.NodeIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;

public class ClusterUtils {

    private static Logger logger = LoggerFactory.getLogger(ClusterUtils.class);

    private static HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Send a join request to a seed node
    public static void tryJoinCluster(ClusterNode node, String seedNodePrefix) throws InterruptedException {

        final int currNodeIdx = node.getNodeIdx();
        int maxNodeIdx = 5;
        int rounds = 0;
        while (rounds <= 5) {
            rounds++;
            logger.info("Attempt to join cluster {}", rounds);
            for (int i = 0; i <= maxNodeIdx; i++) {
                if (i == currNodeIdx) {
                    continue; // skip self join
                }
                String joinEndpoint = NodeIdUtils.nodeEndpointFromIndex(i, node.getPort());
                if (joinCluster(node, joinEndpoint)) {
                    return;
                }
            }
            Thread.sleep(Duration.ofSeconds(3));
        }
    }

    public static boolean joinCluster(ClusterNode node, String joinEndpoint) {
        try {
            String joinApiUrl = joinEndpoint + "/cluster/join";
            logger.info("Attempting to join cluster at {}", joinApiUrl);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(joinApiUrl))
                    .POST(HttpRequest.BodyPublishers.ofString(node.getNodeId()))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String peers = response.body();
                logger.info("Peers from seed {} => {}", joinEndpoint, peers);
                Arrays.stream(peers.split(",")).forEach(node::addNode);
                return true;
            } else {
                logger.info("Invalid response in join cluster {}", response.statusCode());
                return false;
            }
        } catch (Exception e) {
            // ignore all exceptions
            logger.error("Exception occurred while joining with seed {}", joinEndpoint, e);
            return false;
        }
    }

    public static void refreshPeers(ClusterNode node) {
        logger.info("Refresh peer task started with {} peers", node.getPeerNodes());
        for (String peer : node.getPeerNodes()) {
            int peerNodeId = NodeIdUtils.getNodeIndex(peer);
            String peersEndpoint = NodeIdUtils.nodeEndpointFromIndex(peerNodeId, node.getPort());
            if (!joinCluster(node, peersEndpoint)) {
                logger.error("Unreachable peer removed {}", peer);
                node.removeNode(peer);
            }
            // refreshPeers(node, peersEndpoint);
        }
    }

    private static void refreshPeers(ClusterNode node, String peerEndPoint) throws IOException, InterruptedException {
        try {

            String peersApiUrl = peerEndPoint + "/cluster/peers";
            logger.info("Attempting to refresh peers from {}", peersApiUrl);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(peersApiUrl))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String peers = response.body();
                logger.info("Peers from refresh {}", peers);
                Arrays.stream(peers.split(",")).forEach(node::addNode);
            } else {
                logger.error("Invalid response code in refresh peers {}", response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Exception occurred while connecting to peer {}", peerEndPoint, e);
            throw e;
        }
    }
}
