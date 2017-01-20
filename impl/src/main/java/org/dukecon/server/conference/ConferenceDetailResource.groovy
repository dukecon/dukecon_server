package org.dukecon.server.conference

import org.dukecon.model.Conference
import org.dukecon.model.Event
import org.dukecon.model.MetaData
import org.dukecon.model.Speaker

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import io.swagger.annotations.*;


/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Api(hidden = true, value="/conferences", description = "Conferences endpoint")
@Produces(MediaType.APPLICATION_JSON)
class ConferenceDetailResource {
    Conference conference

    ConferenceDetailResource(Conference conference) {
        this.conference = conference
    }

    @GET
    @ApiOperation(value="returns full conference data",
            response = Conference.class)
    public Response getConference() {
        return Response.ok().entity(conference).build();
    }

    @GET
    @Path("speakers")
    @ApiOperation(value="returns list of conference speakers",
            response = Speaker.class,
            responseContainer = "List")
    public Response getSpeakers() {
        return Response.ok().entity(conference.speakers).build();
    }

    @GET
    @Path("events")
    @ApiOperation(value="returns list of conference events",
            response = Event.class,
            responseContainer = "List")
    public Response getEvents() {
        return Response.ok().entity(conference.events).build();
    }

    @GET
    @Path("metadata")
    @ApiOperation(value="returns list of conference meta data",
            response = MetaData.class)
    public Response getMeta() {
        return Response.ok().entity(conference.metaData).build();
    }

    @GET
    @Path("eventSlices")
    @Deprecated
    public Response getEventSlices() {
        Map<Event, List<String>> slices = conference.events.inject([:]) { map, Event e -> map[e.id] = SliceEventHelper.timeSlotsOf(e); map }
        return Response.ok().entity(slices.findAll { k, v -> v.size() > 1 }).build();
    }

    @GET
    @Path("slicedEvents")
    @Deprecated
    public Response getSlicedEvents() {
        List<Event> slicedEvents = conference.events.collect { Event e -> SliceEventHelper.sliceEvent(e) }.flatten()
        return Response.ok().entity(slicedEvents).build();
    }
}
