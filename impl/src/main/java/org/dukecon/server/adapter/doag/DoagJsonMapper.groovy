package org.dukecon.server.adapter.doag

import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.dukecon.server.adapter.MultipleRawDataResources
import org.dukecon.server.adapter.RawDataMapper
import org.dukecon.server.adapter.RawDataResourceSupplier

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class DoagJsonMapper implements RawDataMapper {
    private final Map<String, Object> rawData

    DoagJsonMapper(MultipleRawDataResources resourceSupplier) {
        rawData = parseResources(resourceSupplier)
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private Map<String, Object> parseResources(MultipleRawDataResources resourceSupplier) {
        resourceSupplier.get().collectEntries {k, v ->
            [(k): new JsonSlurper().parse(v.get(), "ISO-8859-1").hits.hits._source]
        }
    }

    @Override
    @TypeChecked(TypeCheckingMode.SKIP)
    void useBackup(RawDataResourceSupplier resourceSupplier) {
        rawData.events = new JsonSlurper().parse(resourceSupplier.get(), "ISO-8859-1").events
    }

    @Override
    Map<String, Object> asMap() {
        return rawData
    }
}
