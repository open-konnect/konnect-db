package org.konnect.storage;

import org.konnect.cluster.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class ProxyStore implements BaseStorage {

    private static Logger logger = LoggerFactory.getLogger(ProxyStore.class);

    private static HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    private Node proxyNode;

    public ProxyStore(String proxyNodeName) {
        this.proxyNode = new Node(proxyNodeName);
    }

    @Override
    public String read(String namespace, String key) {
        try {
            String api = String.format(this.proxyNode.getApiEndpoint() + "/records/%s?namespace=%s", key, namespace);
            logger.info("Attempting to proxy request to peer at {}", api);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(api))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String value = response.body();
                logger.info("Value from endpoint {} => {}", api, value);
                return value;
            } else {
                logger.info("Invalid response {} on proxy read at {}", response.statusCode(), api);
                return null;
            }
        } catch (Exception e) {
            // ignore all exceptions
            logger.error("Exception occurred in proxying read request to {}", this.proxyNode.getName(), e);
            return null;
        }
    }

    @Override
    public void write(String namespace, String key, String value) {
        try {
            String api = String.format(this.proxyNode.getApiEndpoint() + "/records/%s/%s?namespace=%s", key, value, namespace);
            logger.info("Attempting to proxy request to peer at {}", api);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(api))
                    .PUT(HttpRequest.BodyPublishers.ofString(null))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                logger.info("Success in writing to proxy {}", proxyNode.getName());
            } else {
                logger.info("Invalid response in writing to proxy {} at {}", response.statusCode(), api);
            }
        } catch (Exception e) {
            // ignore all exceptions
            logger.error("Exception occurred in proxying write request to {}", this.proxyNode.getName(), e);
        }
    }

    @Override
    public void delete(String namespace, String key) {
        try {
            String api = String.format(this.proxyNode.getApiEndpoint() + "/records/%s?namespace=%s", key, namespace);
            logger.info("Attempting to proxy request to peer at {}", api);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(api))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                logger.info("Success in deleting from proxy {}", proxyNode.getName());
            } else {
                logger.info("Invalid response in deleting from proxy {} at {}", response.statusCode(), api);
            }
        } catch (Exception e) {
            // ignore all exceptions
            logger.error("Exception occurred in proxying delete request to {}", this.proxyNode.getName(), e);
        }
    }

    @Override
    public List<String> scan(String namespace, String startKey, String endKey) {
        return List.of();
    }

    @Override
    public void compaction() {

    }

    @Override
    public void close() {

    }
}
