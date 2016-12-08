package org.dukecon.server.adapter

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DefaultRawDataResource implements RawDataResourceSupplier {

    private final rawDataInput

    DefaultRawDataResource(String resource) {
        rawDataInput = resource.startsWith('http') ? new URL(resource) : this.class.getResourceAsStream("/${resource}")
    }

    @Override
    Object get() {
        return rawDataInput
    }
}
