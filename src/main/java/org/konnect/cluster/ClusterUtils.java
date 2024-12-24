package org.konnect.cluster;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class ClusterUtils {
    // Send a join request to a seed node
    public static void joinCluster(Node node, String seedNode) throws IOException {
        URL url = new URL("http://" + seedNode + "/join");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // Send this node's ID as the body
        try (OutputStream os = conn.getOutputStream()) {
            os.write((node.getHost() + ":" + node.getPort()).getBytes());
        }

        // Get the response (list of peers)
        if (conn.getResponseCode() == 200) {
            String response = new String(conn.getInputStream().readAllBytes());
            Arrays.stream(response.split(",")).forEach(node::addPeer);
        }
    }
}
