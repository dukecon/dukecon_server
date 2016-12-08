package org.dukecon.server.adapter.doag

import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.dukecon.server.adapter.RawDataMapper
import org.dukecon.server.adapter.RawDataResourceSupplier

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class DoagJsonMapper implements RawDataMapper {
    private final Map<String, Object> rawData = [:]

    @TypeChecked(TypeCheckingMode.SKIP)
    DoagJsonMapper(RawDataResourceSupplier... resourceSuppliers) {
        rawData.events = new JsonSlurper().parse(resourceSuppliers.first().get(), "ISO-8859-1").hits.hits._source
    }

    @Override
    Map<String, Object> asMap() {
        return rawData
    }
}
