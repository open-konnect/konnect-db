package org.konnect.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NodeUtilsTest {

    @Test
    public void testHappyCases() {
        Assertions.assertEquals(0, NodeIdUtils.getNodeIndex("konnect-db-0"));
        Assertions.assertEquals(12, NodeIdUtils.getNodeIndex("konnect-db-12"));
        Assertions.assertEquals(999, NodeIdUtils.getNodeIndex("konnect-db-999"));

        Assertions.assertEquals(1, NodeIdUtils.getNodeIndex("http://konnect-db-1.konnect-db:9090"));
        Assertions.assertEquals(0, NodeIdUtils.getNodeIndex("konnect-db-0.konnect-db"));
    }

    @Test
    public void testNegativeCases() {
        Assertions.assertEquals(-1, NodeIdUtils.getNodeIndex("konnect-db-"));
        Assertions.assertEquals(-1, NodeIdUtils.getNodeIndex("-db-12"));
        Assertions.assertEquals(-1, NodeIdUtils.getNodeIndex("konnects-db-0"));
    }
}
