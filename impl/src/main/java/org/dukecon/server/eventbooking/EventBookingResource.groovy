package org.dukecon.server.eventbooking

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.dukecon.model.Event
import org.dukecon.server.repositories.ConferenceDataProvider
import org.dukecon.server.favorites.PreferencesService
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
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

    @GET
    Response getBookingInformation(@PathParam("conferenceId") String conferenceId) {
        def allCapacities = bookingService.getAllCapacities(conferenceId)
        Map<String, Integer> eventIdToNumberOfFavorites = preferencesService.getAllEventFavorites()
        def events = conferences[conferenceId]?.events?.collect { Event e ->
            allCapacities*.eventId.contains(e.id) ?
                    allCapacities.find {e.id == it.eventId}.withNumberOfFavoritesAndLocationCapacity((eventIdToNumberOfFavorites[e.id] ?: 0) as int, e.location?.capacity ?: 0)
                    : new EventBooking(conferenceId: conferenceId, eventId: e.id, locationCapacity: e.location?.capacity, numberOfFavorites: eventIdToNumberOfFavorites[e.id] ?: 0)
        }
        return Response.ok(events).build()
    }

    @GET
    @Path("{eventId}")
    EventBooking getBookInformation(@PathParam("conferenceId") String conferenceId, @PathParam("eventId") String eventId) {
        Event event = getEvent(conferenceId, eventId)
        bookingService.getCapacity(conferenceId, eventId) ?: new EventBooking(conferenceId: conferenceId, eventId: eventId, locationCapacity: event.location?.capacity, numberOfFavorites: preferencesService.allEventFavorites[eventId] ?: 0)
    }

    private Event getEvent(String conferenceId, String eventId) {
        conferences[conferenceId].events.find {e -> e.id == eventId}
    }

    static class EventCapacityInput {
        int numberOccupied = 0
        Boolean fullyBooked

        String toString() {
            "$numberOccupied occupied, fully booked: $fullyBooked"
        }
    }

    /**
     * Sets number of occupied seats for an event.
     *
     * @param eventId
     * @param capacity e.g. {"numberOccupied":850,"fullyBooked":false}, both fields are optional, default is 0 and false
     * @return response
     */
    @POST
    @Path("{eventId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setCapacity(@PathParam("conferenceId") String conferenceId, @PathParam("eventId") String eventId, EventCapacityInput capacity) {
        return Response.status(this.bookingService.setCapacity(conferenceId, eventId, capacity) ? Response.Status.NO_CONTENT : Response.Status.CREATED).entity(capacity).build()
    }
}
