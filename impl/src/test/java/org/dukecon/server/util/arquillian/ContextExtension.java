package org.dukecon.server.util.arquillian;


import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class ContextExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(ResourceProvider.class, ContextProvider.class);
    }
}
