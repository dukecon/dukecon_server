package org.dukecon.server.favorites

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.Event
import org.dukecon.services.ConferenceService
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@Path("favorites/{conferenceId}")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class FavoritesResource {

    private FavoritesRepository favoritesRepository
    private ConferenceService conferenceService

    @Inject
    FavoritesResource(FavoritesRepository favoritesRepository, ConferenceService conferenceService) {
        this.favoritesRepository = favoritesRepository
        this.conferenceService = conferenceService
    }


    @GET
    Response getFavorites(@PathParam("conferenceId") String id) {
        def conference = conferenceService.getConference(id)
        def eventIds = conference.events.id
        def events = favoritesRepository.getAllFavoritesPerEvent(eventIds)
        events.each { e ->
            Event event = conference.events.find {it.id == e.eventId}
            e.title = event?.title
            e.speakers = event?.speakers?.name?.join(', ')
            e.location = event?.location?.names['de']
            e.locationCapacity = event?.location?.capacity
            e.start = event.start
        }
        Response.ok().entity(events).build();
    }

}
