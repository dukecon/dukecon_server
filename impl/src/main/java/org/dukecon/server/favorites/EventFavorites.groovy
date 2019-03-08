package org.dukecon.server.favorites

import java.time.LocalDateTime

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class EventFavorites {
    final String eventId
    final Long numberOfFavorites
    String title
    String speakers
    LocalDateTime start
    String location
    Integer locationCapacity

    EventFavorites(String eventId, Long numberOfFavorites) {
        this.eventId = eventId
        this.numberOfFavorites = numberOfFavorites
    }
}
