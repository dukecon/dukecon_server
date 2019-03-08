package org.dukecon.server.admin

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.dukecon.model.Event
import org.dukecon.server.eventbooking.EventBookingResource
import org.dukecon.server.eventbooking.EventBookingService
import org.dukecon.server.favorites.EventFavorites
import org.dukecon.server.favorites.FavoritesRepository
import org.dukecon.server.repositories.ConferenceDataProvider
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Admin resource contains several endpoints for administration needs.
 *
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@Path("admin/{conferenceId}")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class AdminResource {

    private final EventBookingService service
    private final EventBookingResource resource
    private Map<String, ConferenceDataProvider> talkProviders = new HashMap<>()
    private final FavoritesRepository favoritesRepository

    @Inject
    AdminResource(
            final EventBookingService service,
            final EventBookingResource resource, final List<ConferenceDataProvider> talkProviders, final FavoritesRepository favoritesRepository) {
        talkProviders.each {
            this.talkProviders[it.conferenceId] = it
        }
        this.service = service
        this.resource = resource
        this.favoritesRepository = favoritesRepository
    }

    @GET
    Response getAllCapacities(@PathParam("conferenceId") String conferenceId) {
        return Response.ok().entity(service.getAllCapacities(conferenceId)).build()
    }

    /**
     * @deprecated use #setCapacity instead
     * @param eventId
     * @return
     */
    @POST
    @Path("{eventId}")
    @Deprecated
    public Response setFull(@PathParam("conferenceId") String conferenceId, @PathParam("eventId") String eventId) {
        return resource.setCapacity(conferenceId, eventId, new EventBookingResource.EventCapacityInput(fullyBooked: true))
    }

    /**
     * @deprecated use #setCapacity instead
     * @param eventId
     * @return
     */
    @DELETE
    @Path("{eventId}")
    @Deprecated
    public Response removeFull(@PathParam("conferenceId") String conferenceId, @PathParam("eventId") String eventId) {
        return resource.setCapacity(conferenceId, eventId, new EventBookingResource.EventCapacityInput(fullyBooked: false))
    }

    @GET
    @Path("update")
    Response updateConference(@PathParam("conferenceId") String id) {
        try {
            if (talkProviders[id] == null)
                return Response.status(Response.Status.NOT_FOUND).build()
            if (talkProviders[id].update()) {
                return Response.ok().entity([message: "ok"]).build()
            }
            if (talkProviders[id].isBackupActive()) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity([message: "backup active"]).build()
            }
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity([message: talkProviders[id].staleException.toString()]).build()
        } catch (RuntimeException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity([message: e.toString()]).build()
        }
    }

    @GET
    @Path("favorites")
    Response getFavorites(@PathParam("conferenceId") String id) {
        def conference = talkProviders[id].conference
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