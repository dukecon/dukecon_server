package org.dukecon.server.favorites

import com.fasterxml.jackson.annotation.JsonFormat

/**
 * Data transfer object with informations about favorites per event.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class EventFavorites {
    final String eventId
    final Long numberOfFavorites
    String title
    String speakers
    /** Could not use LocalDateTime because Jackson CSV is not able to bind Java 8 date types at the moment */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date start
    String location
    Integer locationCapacity

    EventFavorites(String eventId, Long numberOfFavorites) {
        this.eventId = eventId
        this.numberOfFavorites = numberOfFavorites
    }
}
