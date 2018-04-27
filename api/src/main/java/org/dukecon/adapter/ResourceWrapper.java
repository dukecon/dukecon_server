package org.dukecon.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Wraps a resource name from a @{@link String}, @{@link File} or @{@link URL} and returns an @{@link InputStream} on access.
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@FunctionalInterface
public interface ResourceWrapper {

    /**
     * @param url
     * @return wrapped @{@link URL} resource
     */
    static ResourceWrapper of(final URL url) {
        return new ResourceWrapper() {
            @Override
            public InputStream getStream() throws IOException {
                return url.openStream();
            }

            @Override
            public String name() {
                return String.format("URL: %s", url.toString());
            }
        };
    }

    /**
     * @param file
     * @return wrapped @{@link File} resource
     */
    static ResourceWrapper of(final File file) {
        return new ResourceWrapper() {
            @Override
            public InputStream getStream() throws IOException {
                return new FileInputStream(file);
            }

            @Override
            public String name() {
                return String.format("File: %s", file.getName());
            }
        };
    }

    /**
     * Wraps an @{@link URL}, if resource name starts with 'http'
     * Wraps a @{@link File}, it resource name starts with 'file:'
     * Otherwise remembers the name and loads resource as stream @{@link Class#getResourceAsStream(String)} with trailing slash ('/') as requested.
     *
     * @param resource
     * @return wraps @{@link URL}, @{@link File} from resource name.
     */
    static ResourceWrapper of(final String resource) {
        return new ResourceWrapper() {
            @Override
            public InputStream getStream() throws IOException {
                return resource.startsWith("http") ? ResourceWrapper.of(new URL(resource)).getStream() : resource.startsWith("file:") ? ResourceWrapper.of(new File(resource.split(":", 2)[1])).getStream() : ResourceWrapper.class.getClassLoader().getResourceAsStream(resource);
            }

            @Override
            public String name() {
                return String.format("Resource: %s", resource);
            }
        };
    }

    /**
     * Creates @{@link InputStream} of wrapped resource on premise.
     *
     * @return inputstream of wrapped resource
     * @throws IOException if inputstream could not be loaded from resource name
     */
    InputStream getStream() throws IOException;

    default String name() {
        return "unknown";
    }
}
