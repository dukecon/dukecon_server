package org.dukecon.server.herbstcampus

import org.dukecon.model.Audience

/**
 * Extracts audience information from CSV input and works as lookup for event mapping.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HerbstcampusAudienceMapper extends HerbstcampusAbstractEntityMapper<Audience> {
    HerbstcampusAudienceMapper(input) {
        super(input, 'Level')
    }
}
