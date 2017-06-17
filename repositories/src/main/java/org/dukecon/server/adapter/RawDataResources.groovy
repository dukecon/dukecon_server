package org.dukecon.server.adapter

import org.dukecon.adapter.ResourceWrapper

import java.util.function.Supplier

/**
 * Takes input resources from configuration as single String or Map with multiple Strings and wraps resource strings in
 * @{@link ResourceWrapper} as map values. In case of a single resource string the only map key will be 'eventsData'.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class RawDataResources implements Supplier<Map<String, ResourceWrapper>> {

    private final Map<String, ResourceWrapper> resources

    RawDataResources(String input) {
        resources = [eventsData: ResourceWrapper.of(input)]
    }

    RawDataResources(Map<String, String> input) {
        resources = input.collectEntries {k,v -> [(k): ResourceWrapper.of(v)]}
    }

    @Override
    Map<String, ResourceWrapper> get() {
        return resources
    }
}
