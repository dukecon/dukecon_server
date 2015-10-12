package org.dukecon.server.service

import org.dukecon.model.Conference
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
@Produces(MediaType.APPLICATION_JSON)
class ConferenceDetailResource {
    Conference conference

    ConferenceDetailResource(Conference conference) {
        this.conference = conference
    }

    @GET
    public Response getConference() {
        return Response.ok().entity(conference).build();
    }

    @GET
    @Path("speakers")
    public Response getSpeakers() {
        println id
        return Response.ok().entity(conference.speakers).build();
    }

    @GET
    @Path("events")
    public Response getEvents() {
        return Response.ok().entity(conference.events).build();
    }

    @GET
    @Path("metadata")
    public Response getMeta() {
        return Response.ok().entity(conference.metaData).build();
    }

}
