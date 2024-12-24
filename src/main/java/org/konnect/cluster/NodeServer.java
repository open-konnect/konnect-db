package org.konnect.cluster;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.InetSocketAddress;

public class NodeServer {
    private final Node node;
    private final HttpServer server;

    public NodeServer(Node node) throws IOException {
        this.node = node;
        this.server = HttpServer.create(new InetSocketAddress(node.getPort()), 0);

        // Register HTTP handlers
        server.createContext("/join", this::handleJoin);
        server.createContext("/peers", this::handlePeers);
    }

    // Start the server
    public void start() {
        server.start();
        System.out.println("Node " + node.getId() + " started on port " + node.getPort());
    }

    // Stop the server
    public void stop() {
        server.stop(0);
    }

    // Handle /join requests for new nodes
    private void handleJoin(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String peer = new String(exchange.getRequestBody().readAllBytes());
            node.addPeer(peer);

            // Send back the current list of peers
            String response = String.join(",", node.getKnownPeers());
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        } else {
            exchange.sendResponseHeaders(405, 0); // Method Not Allowed
        }
        exchange.close();
    }

    // Handle /peers requests to get the list of known peers
    private void handlePeers(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String response = String.join(",", node.getKnownPeers());
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        } else {
            exchange.sendResponseHeaders(405, 0); // Method Not Allowed
        }
        exchange.close();
    }
}
