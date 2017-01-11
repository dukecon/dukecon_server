package org.dukecon.server.adapter

/**
 * Encapsulates a resource handle (file, url) and a parseable instance (#java.net.URL, #java.io.InputStream.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DefaultRawDataResource implements RawDataResourceSupplier {

    private final rawDataInput

    DefaultRawDataResource(resource) {
        rawDataInput = resource.startsWith('http') ? new URL(resource) : resource.startsWith('file:') ? new File(resource).newInputStream() : this.class.getResourceAsStream("/${resource}")
    }

    @Override
    Object get() {
        return rawDataInput
    }
}
