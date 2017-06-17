package org.dukecon.server.adapter.heise

import org.dukecon.model.Track

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HeiseStreamMapper extends HeiseAbstractEntityMapper<Track> {
    HeiseStreamMapper(input) {
        super(input, 'Kategorie')
    }

    @Override
    protected String calculateValue(String value) {
        return value.size() > 2 ? value.substring(3) : value
    }
}
