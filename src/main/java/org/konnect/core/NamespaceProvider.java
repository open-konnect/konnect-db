package org.konnect.core;

import java.util.List;

public interface NamespaceProvider {

    List<String> listNamespaces();

    boolean namespaceExists(String namespace);

    void createNamespace(String namespace);

    void deleteNamespace(String namespace);
}
