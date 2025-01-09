package org.konnect.cluster;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RoutingInfo {
    private boolean isPresentInSelf;
    private List<String> peerNodes;
}
