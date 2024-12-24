package org.konnect.api;

import org.konnect.core.AbstractKeyValueStore;
import org.konnect.utils.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/records")
public class KeyValueController {

    private static final String DEFAULT_NAMESPACE = "default";

    private AbstractKeyValueStore keyValueStore;

    public KeyValueController(AbstractKeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
    }

    @PostMapping("/{key}/{value}")
    public ResponseEntity<Void> put(@PathVariable String key, @PathVariable String value, @RequestParam(required = false) String namespace) {
        namespace = StringUtils.defaultIfBlank(namespace, DEFAULT_NAMESPACE);
        keyValueStore.put(namespace, key, value);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{key}")
    public ResponseEntity<String> get(@PathVariable String key, @RequestParam(required = false) String namespace) {
        namespace = StringUtils.defaultIfBlank(namespace, DEFAULT_NAMESPACE);
        String value = keyValueStore.get(namespace, key);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key, @RequestParam(required = false) String namespace) {
        namespace = StringUtils.defaultIfBlank(namespace, DEFAULT_NAMESPACE);
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
