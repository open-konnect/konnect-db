package org.konnect.cluster;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class PeerSyncTask implements Runnable {
    private final Node node;

    public PeerSyncTask(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        for (String peer : node.getKnownPeers()) {
            try {
                URL url = new URL("http://" + peer + "/peers");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    String response = new Scanner(conn.getInputStream()).useDelimiter("\\A").next();
                    for (String newPeer : response.split(",")) {
                        if (!newPeer.equals(node.getHost() + ":" + node.getPort())) {
                            node.addPeer(newPeer);
                        }
                    }
                }
            } catch (Exception e) {
                // Handle unreachable peer (e.g., remove it from the list)
                node.removePeer(peer);
            }
        }
    }
}
