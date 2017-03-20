package org.dukecon.server.admin

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@Path("admin/fullyBooked")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class FullyBookedResource {

    private final FullyBookedService service

    @Inject
    FullyBookedResource(final FullyBookedService service) {
        this.service = service
    }

    @GET
    Response getAllFullyBooked() {
        return Response.ok().entity(service.getFullyBooked()).build()
    }

    @POST
    @Secured("ROLE_ADMIN")
    public Response setFull(String eventId) {
        if (this.service.isFull(eventId)) {
            return Response.status(Response.Status.NO_CONTENT).build()
        }
        this.service.setFull(eventId);
        return Response.status(Response.Status.CREATED).build()
    }

    @DELETE
    @Secured("ROLE_ADMIN")
    public Response removeFull(String eventId) {
        if (this.service.isFull(eventId)) {
            this.service.removeFull(eventId);
            return Response.status(Response.Status.OK).build()
        }
        return Response.status(Response.Status.NOT_FOUND).build()
    }
}