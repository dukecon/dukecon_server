package org.dukecon.server.admin

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
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

    @Inject
    AdminResource(final EventBookingService service) {
        this.service = service
    }

    @GET
    Response getAllFullyBooked(@PathParam("conferenceId") String atTheMomentIgnoredConferenceId) {
        return Response.ok().entity(service.getFullyBooked()).build()
    }

    @POST
    @Path("{eventId}")
    public Response setFull(@PathParam("eventId") String eventId) {
        if (this.service.isFull(eventId)) {
            return Response.status(Response.Status.NO_CONTENT).build()
        }
        this.service.setFull(eventId);
        return Response.status(Response.Status.CREATED).build()
    }

    @DELETE
    @Path("{eventId}")
    public Response removeFull(@PathParam("eventId") String eventId) {
        if (this.service.isFull(eventId)) {
            this.service.removeFull(eventId);
            return Response.status(Response.Status.OK).build()
        }
        return Response.status(Response.Status.NOT_FOUND).build()
    }
}