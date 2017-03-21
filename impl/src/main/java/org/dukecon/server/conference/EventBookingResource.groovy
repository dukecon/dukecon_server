package org.dukecon.server.conference

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.dukecon.model.Event
import org.dukecon.server.adapter.ConferenceDataProvider
import org.dukecon.server.admin.EventBookingService
import org.dukecon.server.favorites.PreferencesService
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
@Path("eventsBooking/{conferenceId}")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class EventBookingResource {
    private final Map<String, Conference> conferences
    private final EventBookingService bookingService
    private final PreferencesService preferencesService

    @Inject
    EventBookingResource(
            final EventBookingService bookingService,
            final List<ConferenceDataProvider> talkProviders, final PreferencesService preferencesService) {
        this.preferencesService = preferencesService
        this.bookingService = bookingService
        conferences = talkProviders.collectEntries { [it.conferenceId, it.conference] }
    }

    static class EventBooking {
        String eventId
        int numberOfFavorites
        boolean fullyBooked
    }

    @GET
    Response getBookingInformation(@PathParam("conferenceId") String conferenceId) {
        def events = conferences[conferenceId].events.collect { Event e ->
            new EventBooking(eventId: e.id, numberOfFavorites: preferencesService.allEventFavorites[e.id] ?: 0, fullyBooked: bookingService.isFull(e.id))
        }
        return Response.ok(events).build()
    }


}
