package org.konnect.cluster;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class ClusterServer {

    private static Logger log = LoggerFactory.getLogger(ClusterServer.class);

    private final SelfNode node;
    private final HttpServer server;

    public ClusterServer(SelfNode selfNode) throws IOException {
        //this.node = SelfNode.INSTANCE;
        this.node = selfNode;
        this.server = HttpServer.create(new InetSocketAddress(ClusterConstants.NODE_PORT), 0);

        // Register HTTP handlers
        server.createContext("/cluster/join", this::handleJoin);
        server.createContext("/cluster/peers", this::handlePeers);
        server.createContext("/cluster/health", this::handleHealth);
        server.createContext("/cluster/ready", this::handleHealth);
    }

    // Start the server
    public void start() {
        server.start();
        log.info("Node cluster server started on {}", node.getNodeEndpoint());
    }

    // Stop the server
    public void shutdown() {
        log.info("Stopping server on {}", node.getNodeEndpoint());
        server.stop(0);
    }

    private void handleHealth(HttpExchange exchange) {
        try {
            //log.info("Health check on node {}", node.getNodeId());
            exchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        exchange.close();
    }

    // Handle /join requests for new nodes
    private void handleJoin(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String peer = new String(exchange.getRequestBody().readAllBytes());
            log.info("Request received to join cluster from {}", peer);
            node.addNode(peer);

            // Send back the current list of peers
            String response = String.join(",", node.getAllNodes());
            log.info("Responding with known peers (Join Request) => {}", response);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        } else {
            log.error("Incorrect http method to join cluster {}", exchange.getRequestMethod());
            exchange.sendResponseHeaders(405, 0); // Method Not Allowed
        }
        exchange.close();
    }

    // Handle /peers requests to get the list of known peers
    private void handlePeers(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            log.info("Get Peer request received");
            String response = String.join(",", node.getAllNodes());
            log.info("Responding with known peers => {}", response);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        } else {
            log.error("Incorrect http method to find peers {}", exchange.getRequestMethod());
            exchange.sendResponseHeaders(405, 0); // Method Not Allowed
        }
        exchange.close();
    }
}
