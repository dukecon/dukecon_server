package org.dukecon.server.favorites

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.dukecon.model.Event
import org.springframework.stereotype.Service

import javax.inject.Inject
import java.time.ZoneOffset

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Service
@TypeChecked
@Slf4j
class FavoritesService {
    private FavoritesRepository favoritesRepository

    @Inject
    FavoritesService(FavoritesRepository favoritesRepository) {
        this.favoritesRepository = favoritesRepository
    }

    /**
     * Returns a list of favorites per event with meta data (title, speaker, location, locationCapacity and start time.
     *
     * @param conference for which event favorites should be returned
     * @return List of EventFavorites
     */
    List<EventFavorites> getAllFavoritesForConference(Conference conference) {
        def eventIds = conference.events.id
        def events = favoritesRepository.getAllFavoritesPerEvent(eventIds)

        events.each { e ->
            Event event = conference.events.find { it.id == e.eventId }
            e.title = event?.title
            e.speakers = event?.speakers?.name?.join(', ')
            e.location = event?.location?.names['de']
            e.locationCapacity = event?.location?.capacity
            e.start = Date.from(event.start.toInstant(ZoneOffset.UTC))
        }
        events.sort { e1, e2 -> e1.start <=> e2.start ?: e2.numberOfFavorites <=> e1.numberOfFavorites ?: e1.title <=> e2.title }

        return events
    }
}
