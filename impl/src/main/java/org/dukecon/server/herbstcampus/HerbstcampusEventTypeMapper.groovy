package org.dukecon.server.herbstcampus

import org.dukecon.model.EventType

/**
 * Extracts location information from CSV input and works as location lookup for event mapping.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HerbstcampusEventTypeMapper extends HerbstcampusAbstractEntityMapper<EventType> {
    HerbstcampusEventTypeMapper(input) {
        super(input, 'Art')
    }
}
