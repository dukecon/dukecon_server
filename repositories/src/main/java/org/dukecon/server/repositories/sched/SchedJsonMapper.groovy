package org.dukecon.server.repositories.sched

import groovy.transform.TypeChecked
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.server.repositories.RawDataMapper

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class SchedJsonMapper implements RawDataMapper {


    SchedJsonMapper(ResourceWrapper... resourceWrapper) {

    }

    @Override
    Map<String, Object> asMap() {
        return null
    }

    @Override
    void initMapper() {

    }

    @Override
    void useBackup(ResourceWrapper resourceWrapper) {
        // TODO implement
    }
}
