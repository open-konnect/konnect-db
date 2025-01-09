package org.konnect.beans;

import org.konnect.cluster.ClusterConstants;
import org.konnect.cluster.ClusterServer;
import org.konnect.cluster.Node;
import org.konnect.cluster.SelfNode;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

@Configuration
public class ClusterBeans {

    @Bean(initMethod = "bootstrap")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SelfNode selfNode() {
        return new SelfNode(new Node(ClusterConstants.SELF_NODE_ID));
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ClusterServer clusterServer(SelfNode selfNode) throws IOException {
        ClusterServer server = new ClusterServer(selfNode);
        return server;
    }

}
