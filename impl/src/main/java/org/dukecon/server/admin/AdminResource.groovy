package org.dukecon.server.admin

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.server.eventbooking.EventBookingResource
import org.dukecon.server.eventbooking.EventBookingService
import org.dukecon.server.repositories.ConferenceDataProvider
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

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

    @Inject
    AdminResource(
            final EventBookingService service,
            final EventBookingResource resource, final List<ConferenceDataProvider> talkProviders) {
        talkProviders.each {
            this.talkProviders[it.conferenceId] = it
        }
        this.service = service
        this.resource = resource
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

}