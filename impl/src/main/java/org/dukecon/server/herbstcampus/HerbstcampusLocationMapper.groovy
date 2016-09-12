package org.dukecon.server.herbstcampus

import org.dukecon.model.Location

/**
 * Extracts location information from CSV input and works as location lookup for event mapping.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HerbstcampusLocationMapper  extends HerbstcampusAbstractEntityMapper<Location> {
    HerbstcampusLocationMapper(input) {
        super(input, 'Raum')
    }
}
