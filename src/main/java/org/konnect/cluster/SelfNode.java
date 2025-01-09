package org.konnect.cluster;

import org.konnect.utils.CollectionUtils;
import org.konnect.utils.NodeIdUtils;
import org.konnect.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


public final class SelfNode {

    private static Logger log = LoggerFactory.getLogger(SelfNode.class);

    private final Node node;
    private final List<PeerNode> peerNodeList;
    private final ConsistentHashing consistentHashing;

    public SelfNode(Node node) {
        this.node = node;
        this.peerNodeList = new ArrayList<>();
        this.consistentHashing = new ConsistentHashing();
        this.consistentHashing.addNode(node.getName());
    }

    public String getNodeEndpoint() {
        return this.node.getClusterEndpoint();
    }

    public RoutingInfo getRoutingInfo(String namespace, String key) {
        List<String> nodes = consistentHashing.getNodesForKey(namespace, key);
        if (nodes.contains(this.node.getName())) {
            return RoutingInfo.builder().isPresentInSelf(true).build();
        }
        return RoutingInfo.builder().isPresentInSelf(false).peerNodes(nodes).build();
    }

    // Add a new node to the cluster
    public synchronized void addNode(String node) {
        if (StringUtils.isBlank(node)) {
            return;
        }
        node = node.trim();
        if (this.node.getName().equals(node)) {
            return;
        }

        PeerNode peerNode = new PeerNode(node);
        if (!peerNodeList.contains(peerNode)) {
            peerNodeList.add(peerNode);
            consistentHashing.addNode(node);
            // broadcastClusterUpdate();
        }
    }

    // Remove a node from the cluster
    public synchronized void removeNode(String node) {
        if (StringUtils.isBlank(node)) {
            return;
        }
        node = node.trim();
        PeerNode peerNode = new PeerNode(node);
        if (peerNodeList.contains(peerNode)) {
            peerNodeList.remove(peerNode);
            consistentHashing.removeNode(node);
        }
    }

    public List<String> getAllNodes() {
        List<String> allNodes = peerNodeList.stream().map(p -> p.getNode().getName()).collect(Collectors.toList());
        allNodes.add(this.node.getName());
        return allNodes;
    }

    public void refreshPeers() {
        //log.info("Refresh peer task started with {} peers", this.peerNodeList);
        Set<String> newPeers = new HashSet<>();
        Iterator<PeerNode> itr = this.peerNodeList.iterator();
        while (itr.hasNext()) {
            PeerNode peer = itr.next();
            List<String> refreshedPeers = refreshFromPeer(peer);
            if (CollectionUtils.isNotEmpty(refreshedPeers)) {
                newPeers.addAll(refreshedPeers);
            } else {
                log.error("Unreachable peer removed {}", peer.getNode().getName());
                itr.remove();
                removeNode(peer.getNode().getName());
            }
        }

        //log.info("Refreshed peer task completed with new peers {}", newPeers);
        newPeers.forEach(this::addNode);
    }

    private List<String> refreshFromPeer(PeerNode peerNode) {
        String[] refreshedPeers = peerNode.refreshPeers(this.node.getName());
        if (refreshedPeers != null && refreshedPeers.length > 0) {
            return Arrays.asList(refreshedPeers);
        }
        return null;
    }

    public void bootstrap() throws InterruptedException {
        final int currNodeIdx = node.getIndex();
        int maxNodeIdx = ClusterConstants.MAX_REPLICA;
        int rounds = 0;
        while (rounds <= 5) {
            rounds++;
            log.info("Attempt to join cluster {}", rounds);
            for (int i = 0; i <= maxNodeIdx; i++) {
                if (i == currNodeIdx) {
                    continue; // skip self join
                }
                PeerNode peerNode = new PeerNode(NodeIdUtils.nodeNameFromIndex(i));
                List<String> refreshedPeers = refreshFromPeer(peerNode);
                if (CollectionUtils.isNotEmpty(refreshedPeers)) {
                    log.info("Successfully joined cluster with peer {} Found total {} peers", i, refreshedPeers);
                    refreshedPeers.forEach(this::addNode);
                    return;
                }
            }
            Thread.sleep(Duration.ofSeconds(3));
        }
    }
}
