package org.konnect.cluster;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PeerNode {

    private static Logger logger = LoggerFactory.getLogger(PeerNode.class);

    private static HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Getter private final Node node;

    public PeerNode(String nodeName) {
        this.node = new Node(nodeName);
    }

    public String[] refreshPeers(String currNodeName) {
        try {
            String joinApiUrl = this.node.getClusterEndpoint() + "/cluster/join";
            logger.info("Attempting to join peer at {}", joinApiUrl);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(joinApiUrl))
                    .POST(HttpRequest.BodyPublishers.ofString(currNodeName))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String peers = response.body();
                logger.info("Peers from endpoint {} => {}", this.node.getClusterEndpoint(), peers);
                return peers.split(",");
            } else {
                logger.info("Invalid response in join cluster {}", response.statusCode());
                return null;
            }
        } catch (Exception e) {
            // ignore all exceptions
            logger.error("Exception occurred while joining with seed {}", this.node.getClusterEndpoint(), e);
            return null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PeerNode) {
            return ((PeerNode) other).node.equals(this.node);
        }
        return false;
    }


}
