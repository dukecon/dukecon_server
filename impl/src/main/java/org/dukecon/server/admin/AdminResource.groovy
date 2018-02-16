package org.dukecon.server.admin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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
    Response getAllCapacities(@PathParam("conferenceId") String atTheMomentIgnoredConferenceId) {
        return Response.ok().entity(service.capacities.values()).build()
    }

    /**
     * @deprecated use #setCapacity instead
     * @param eventId
     * @return
     */
    @POST
    @Path("{eventId}")
    @Deprecated
    public Response setFull(@PathParam("eventId") String eventId) {
        if (this.service.isFull(eventId)) {
            return Response.status(Response.Status.NO_CONTENT).build()
        }
        this.service.setFull(eventId);
        return Response.status(Response.Status.CREATED).build()
    }

    /**
     * @deprecated use #setCapacity instead
     * @param eventId
     * @return
     */
    @DELETE
    @Path("{eventId}")
    @Deprecated
    public Response removeFull(@PathParam("eventId") String eventId) {
        if (this.service.isFull(eventId)) {
            this.service.removeFull(eventId);
            return Response.status(Response.Status.OK).build()
        }
        return Response.status(Response.Status.NOT_FOUND).build()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class EventCapacity {
        String eventId
        int numberOccupied
        boolean fullyBooked

        String toString() {
            "$numberOccupied occupied, fully booked: $fullyBooked"
        }
    }

    /**
     * Sets number of occupied seats for a event.
     *
     * @param eventId
     * @param capacity {"numberOccupied":850,"fullyBooked":false}
     * @return
     */
    @POST
    @Path("capacity/{eventId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setCapacity(@PathParam("eventId") String eventId, EventCapacity capacity) {
        println capacity
        this.service.setCapacity(eventId, capacity)
        return Response.status(Response.Status.CREATED).build()
    }
}