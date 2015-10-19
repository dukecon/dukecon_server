package org.dukecon.server.service

import org.dukecon.model.Conference
import org.dukecon.model.Event
import org.dukecon.server.conference.SliceEventHelper

import javax.ws.rs.GET
import javax.ws.rs.Path
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

    @GET
    @Path("eventSlices")
    public Response getEventSlices() {
        Map<Event, List<String>> slices = conference.events.inject([:]) { map, Event e -> map[e.id] = SliceEventHelper.timeSlotsOf(e); map }
        return Response.ok().entity(slices.findAll { k, v -> v.size() > 1 }).build();
    }

    @GET
    @Path("slicedEvents")
    public Response getSlicedEvents() {
        List<Event> slicedEvents = conference.events.collect { Event e -> SliceEventHelper.sliceEvent(e) }.flatten()
        return Response.ok().entity(slicedEvents).build();
    }
}
