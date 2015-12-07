package org.dukecon.server.util.arquillian;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

import java.lang.annotation.Annotation;
import java.net.URL;

public class ContextProvider implements ResourceProvider {

    @Override
    public boolean canProvide(Class<?> type) {
        return URL.class.isAssignableFrom(type);
    }

    private static ThreadLocal<URL> url = new ThreadLocal<>();

    public static void setUrl(URL url) {
        ContextProvider.url.set(url);
    }

    @Override
    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        return url.get();
    }
}
