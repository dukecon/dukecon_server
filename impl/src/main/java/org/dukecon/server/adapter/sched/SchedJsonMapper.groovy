package org.dukecon.server.adapter.sched

import groovy.transform.TypeChecked
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.server.adapter.RawDataMapper

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class SchedJsonMapper implements RawDataMapper {


    SchedJsonMapper(ResourceWrapper... resourceSupplier) {

    }

    @Override
    Map<String, Object> asMap() {
        return null
    }

    @Override
    void useBackup(ResourceWrapper resourceSupplier) {
        // TODO implement
    }
}
