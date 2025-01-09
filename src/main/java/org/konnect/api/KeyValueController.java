package org.konnect.api;

import org.konnect.app.KonnectDBApplication;
import org.konnect.cluster.SelfNode;
import org.konnect.core.AbstractKeyValueStore;
import org.konnect.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/records")
public class KeyValueController {

    private static Logger log = LoggerFactory.getLogger(KeyValueController.class);

    private static final String DEFAULT_NAMESPACE = "default";

    private AbstractKeyValueStore keyValueStore;

    public KeyValueController(AbstractKeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
    }

    @PutMapping("/{key}/{value}")
    public ResponseEntity<Void> put(@PathVariable String key, @PathVariable String value, @RequestParam(required = false) String namespace) {
        namespace = StringUtils.defaultIfBlank(namespace, DEFAULT_NAMESPACE);
        log.info("Request received to update key {} with val {} under namespace {}", key, value, namespace);
        keyValueStore.put(namespace, key, value);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{key}")
    public ResponseEntity<String> get(@PathVariable String key, @RequestParam(required = false) String namespace) {
        namespace = StringUtils.defaultIfBlank(namespace, DEFAULT_NAMESPACE);
        log.info("Request received to get key {} under namespace {}", key, namespace);
        String value = keyValueStore.get(namespace, key);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key, @RequestParam(required = false) String namespace) {
        namespace = StringUtils.defaultIfBlank(namespace, DEFAULT_NAMESPACE);
        log.info("Request received to delete key {} under namespace {}", key, namespace);
        keyValueStore.delete(namespace, key);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/scan")
    public ResponseEntity<List<String>> scan(@RequestParam String startKey, @RequestParam String endKey,
                                             @RequestParam(required = false) String namespace) {
        namespace = StringUtils.defaultIfBlank(namespace, DEFAULT_NAMESPACE);
        List<String> values = keyValueStore.scan(namespace, startKey, endKey);
        return ResponseEntity.ok(values);
    }
}
