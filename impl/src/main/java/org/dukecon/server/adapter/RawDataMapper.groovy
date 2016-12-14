package org.dukecon.server.adapter

import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode

/**
 * Subclasses can map input data (file, url, text) to a resulting map of raw data.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
interface RawDataMapper {
    Map<String, Object> asMap()
    void useBackup(RawDataResourceSupplier resourceSupplier)
}