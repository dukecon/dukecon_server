package org.dukecon.server.adapter.heise

import org.dukecon.model.Audience

/**
 * Extracts audience information from CSV input and works as lookup for event mapping.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HeiseAudienceMapper extends HeiseAbstractEntityMapper<Audience> {
    HeiseAudienceMapper(input) {
        super(input, 'Level')
    }
}
