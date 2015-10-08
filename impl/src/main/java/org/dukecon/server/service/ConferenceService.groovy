package org.dukecon.server.service

import org.dukecon.server.business.JavalandDataProvider
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@Path("conference")
@Produces(MediaType.APPLICATION_JSON)
class ConferenceService {
    @Inject
    JavalandDataProvider talkProvider;

    @GET
    public Response getConference() {
        return Response.ok().entity(talkProvider.conference).build();
    }

    @GET
    @Path("speakers")
    public Response getSpeakers() {
        return Response.ok().entity(talkProvider.conference.speakers).build();
    }

    @GET
    @Path("talks")
    public Response getTalks() {
        return Response.ok().entity(talkProvider.conference.talks).build();
    }

    @GET
    @Path("metadata")
    public Response getMeta() {
        return Response.ok().entity(talkProvider.conference.metaData).build();
    }
}
