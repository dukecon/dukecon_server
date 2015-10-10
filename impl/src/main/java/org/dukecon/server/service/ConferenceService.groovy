package org.dukecon.server.service

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
@Path("conferences")
@Produces(MediaType.APPLICATION_JSON)
class ConferenceService {
    @Inject
    JavalandDataProvider talkProvider;

    @GET
    public Response getConferences() {
        return Response.ok().entity([talkProvider.conference.id]).build();
    }

    @GET
    @Path("{id}")
    public Response getConference(@PathParam("id") String id) {
        return Response.ok().entity(talkProvider.conference).build();
    }

    @GET
    @Path("{id}/speakers")
    public Response getSpeakers(@PathParam("id") String id) {
        return Response.ok().entity(talkProvider.conference.speakers).build();
    }

    @GET
    @Path("{id}/events")
    public Response getTalks(@PathParam("id") String id) {
        return Response.ok().entity(talkProvider.conference.talks).build();
    }

    @GET
    @Path("{id}/metadata")
    public Response getMeta(@PathParam("id") String id) {
        return Response.ok().entity(talkProvider.conference.metaData).build();
    }
}
