package org.dukecon.server.service

import groovy.transform.TypeChecked
import org.dukecon.server.business.JavalandDataProvider
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
@Path("/conferences")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
class ConferencesResource {
    @Inject
    JavalandDataProvider talkProvider;

    @GET
    public Response getConferences() {
        return Response.ok().entity([[id: talkProvider.conference.id, name: talkProvider.conference.name]]).build();
    }

    @Path("/{id}")
    public ConferenceDetailResource getConferenceDetails(@PathParam("id") String id) {
        def conference = talkProvider.conference
        if (conference.id != id) {
            throw new IllegalArgumentException("resource with id $id not found")
        }
        return new ConferenceDetailResource(conference)
    }
}
