package org.dukecon.server.adapter

import java.util.function.Supplier

/**
 * Takes input resources from configuration as single String or Map with multiple Strings and wraps resource strings in
 * @DefaultRawDataResource as map values. In case of a single resource string the only map key will be 'eventsData'.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class MultipleRawDataResources implements RawDataResourceSupplier<Map<String, DefaultRawDataResource>> {
    private final Map<String, DefaultRawDataResource> resources

    MultipleRawDataResources(String input) {
        resources = [eventsData: new DefaultRawDataResource(input)]
    }

    MultipleRawDataResources(Map<String, String> input) {
        resources = input.collectEntries {k,v -> [(k): new DefaultRawDataResource(v)]}
    }

    @Override
    Map<String, DefaultRawDataResource> get() {
        return resources
    }
}
