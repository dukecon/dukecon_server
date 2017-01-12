package org.dukecon.server.adapter.doag

import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.server.adapter.RawDataResources
import org.dukecon.server.adapter.RawDataMapper

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class DoagJsonMapper implements RawDataMapper {
    private final Map<String, Object> rawData

    DoagJsonMapper(RawDataResources resourceSupplier) {
        rawData = parseResources(resourceSupplier)
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private Map<String, Object> parseResources(RawDataResources resourceSupplier) {
        resourceSupplier.get().collectEntries {k, v ->
            [(k): new JsonSlurper().parse(v.getStream(), "ISO-8859-1").hits.hits._source]
        }
    }

    @Override
    @TypeChecked(TypeCheckingMode.SKIP)
    void useBackup(ResourceWrapper resourceSupplier) {
        rawData.events = new JsonSlurper().parse(resourceSupplier.get(), "ISO-8859-1").events
    }

    @Override
    Map<String, Object> asMap() {
        return rawData
    }
}
