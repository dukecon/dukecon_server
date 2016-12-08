package org.dukecon.server.adapter.sched

import groovy.transform.TypeChecked
import org.dukecon.server.adapter.RawDataMapper
import org.dukecon.server.adapter.RawDataResourceSupplier

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class SchedJsonMapper implements RawDataMapper {


    SchedJsonMapper(RawDataResourceSupplier... resourceSupplier) {

    }

    @Override
    Map<String, Object> asMap() {
        return null
    }
}
