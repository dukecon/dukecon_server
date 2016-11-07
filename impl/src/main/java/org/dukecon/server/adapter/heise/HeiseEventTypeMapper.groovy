package org.dukecon.server.adapter.heise

import org.dukecon.model.EventType

/**
 * Extracts location information from CSV input and works as location lookup for event mapping.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HeiseEventTypeMapper extends HeiseAbstractEntityMapper<EventType> {
    HeiseEventTypeMapper(input) {
        super(input, 'Art')
    }
}
