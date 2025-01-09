package org.konnect.beans;

import org.konnect.cluster.SelfNode;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class PeerSyncTask {

    private SelfNode selfNode;

    public PeerSyncTask(SelfNode selfNode) {
        this.selfNode = selfNode;
    }

    @Scheduled(fixedDelay = 10, initialDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void run() {
        this.selfNode.refreshPeers();
        // ClusterUtils.refreshPeers(node);
    }
}
