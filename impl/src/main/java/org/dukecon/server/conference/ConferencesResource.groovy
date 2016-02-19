package org.dukecon.server.conference

import groovy.transform.TypeChecked
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
@Path("conferences")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
class ConferencesResource {
    @Inject
    JavalandDataProvider talkProvider;

    @GET
    public Response getConferences() {
        return Response.ok().entity([[id: talkProvider.conference.id, name: talkProvider.conference.name]]).build();
    }

    @GET
    @Path("update/{id:[0-9]*}")
    public Response updateConference(@PathParam("id") String id) {
        try {
            if (talkProvider.update()) {
                return Response.ok().entity([message: "ok"]).build();
            } else if (talkProvider.remote.isBackupActive()) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity([message: "backup active"]).build();
            } else {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity([message: talkProvider.staleException.toString()]).build();
            }
        } catch (RuntimeException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity([message: e.toString()]).build();
        }
    }

    @GET
    @Path("{id:[0-9]*}")
    public ConferenceDetailResource getConferenceDetails(@PathParam("id") String id) {
        def conference = talkProvider.conference
        if (conference.id != id) {
            throw new IllegalArgumentException("resource with id $id not found")
        }
        return new ConferenceDetailResource(conference)
    }
}
