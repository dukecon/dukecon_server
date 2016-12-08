package org.dukecon.server.adapter

/**
 * Subclasses can map input data (file, url, text) to a resulting map of raw data.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
interface RawDataMapper {
    Map<String, Object> asMap()
}