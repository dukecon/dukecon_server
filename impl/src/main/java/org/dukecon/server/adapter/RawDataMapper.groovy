package org.dukecon.server.adapter

import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.dukecon.adapter.ResourceWrapper

/**
 * Subclasses can map input data (file, url, text) to a resulting map of raw data.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
interface RawDataMapper {
    /**
     * @return raw data from input resources as Map
     */
    Map<String, Object> asMap()
//    Map<String, Object> createBackupData()
    void useBackup(ResourceWrapper resourceSupplier)
}