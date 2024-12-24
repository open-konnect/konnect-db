package org.konnect.core;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryKeyValueStoreTest {
    private InMemoryKeyValueStore keyValueStore;

    @BeforeEach
    public void setUp() {
        keyValueStore = new InMemoryKeyValueStore();
    }

    @Test
    public void testPutAndGet() {
        keyValueStore.put("tenant1", "key1", "value1");
        assertEquals("value1", keyValueStore.get("tenant1", "key1"));
    }

    @Test
    public void testDelete() {
        keyValueStore.put("tenant1", "key1", "value1");
        keyValueStore.delete("tenant1", "key1");
        assertNull(keyValueStore.get("tenant1", "key1"));
    }

    @Test
    public void testScan() {
        keyValueStore.put("tenant1", "key1", "value1");
        keyValueStore.put("tenant1", "key2", "value2");
        assertEquals(2, keyValueStore.scan("tenant1", "key1", "key2").size());
    }
}
