package org.dukecon.server.herbstcampus

import org.dukecon.model.Track

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HerbstcampusStreamMapper extends HerbstcampusAbstractEntityMapper<Track> {
    HerbstcampusStreamMapper(input) {
        super(input, 'Kategorie')
    }

    @Override
    protected String calculateValue(String value) {
        return value.size() > 2 ? value.substring(3) : value
    }
}
