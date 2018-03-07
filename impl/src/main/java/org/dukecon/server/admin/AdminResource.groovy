package org.dukecon.server.admin

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.server.eventbooking.EventBookingResource
import org.dukecon.server.eventbooking.EventBookingService
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

    @Inject
    AdminResource(final EventBookingService service, final EventBookingResource resource) {
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
}