package org.dukecon.server.adapter.heise

import org.dukecon.model.Location

/**
 * Extracts location information from CSV input and works as location lookup for event mapping.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HeiseLocationMapper extends HeiseAbstractEntityMapper<Location> {
    HeiseLocationMapper(input) {
        super(input, 'Raum')
    }
}
