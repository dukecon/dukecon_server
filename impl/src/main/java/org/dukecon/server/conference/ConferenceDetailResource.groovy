package org.dukecon.server.conference

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.dukecon.model.Conference
import org.dukecon.model.Event
import org.dukecon.model.MetaData
import org.dukecon.model.Speaker

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.time.temporal.ChronoField

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Api(hidden = true, value = "/conferences", description = "Conferences endpoint")
@Produces(MediaType.APPLICATION_JSON)
class ConferenceDetailResource {
    Conference conference

    ConferenceDetailResource(Conference conference) {
        this.conference = conference
    }

    @GET
    @ApiOperation(value = "returns full conference data",
            response = Conference.class)
    public Response getConference() {
        return Response.ok().entity(conference).build();
    }

    /**
     * Data type for Canoo Voting machines as CSV export:
     * id,conference,name,type,room,speakers,startYear,startMonth,startDayOfMonth,startHour,startMinute,endYear,endMonth,endDayOfMonth,endHour,endMinute
     */
    private static class ShortEvent {
        String id
        String conference
        String name
        String type
        String room
        String speakers
        int startYear
        int startMonth
        int startDayOfMonth
        int startHour
        int startMinute
        int endYear
        int endMonth
        int endDayOfMonth
        int endHour
        int endMinute

        ShortEvent(Event event, String conferenceId, String lang) {
            this.id = event.id
            this.conference = conferenceId
            this.name = event.title
            this.type = event.type?.names?.get(lang)
            this.room = event.location?.names?.get(lang)
            this.speakers = event.speakers?.name?.join(', ')
            this.startYear = event.start.get(ChronoField.YEAR)
            this.startMonth = event.start.get(ChronoField.MONTH_OF_YEAR)
            this.startDayOfMonth = event.start.get(ChronoField.DAY_OF_MONTH)
            this.startHour = event.start.get(ChronoField.HOUR_OF_DAY)
            this.startMinute = event.start.get(ChronoField.MINUTE_OF_HOUR)
            this.endYear = event.end.get(ChronoField.YEAR)
            this.endMonth = event.end.get(ChronoField.MONTH_OF_YEAR)
            this.endDayOfMonth = event.end.get(ChronoField.DAY_OF_MONTH)
            this.endHour = event.end.get(ChronoField.HOUR_OF_DAY)
            this.endMinute = event.end.get(ChronoField.MINUTE_OF_HOUR)
        }
    }

    /**
     * Return events export (short version) for Canoo Voting Machines
     * @return
     */
    @GET
    @Path("events/short")
    @Produces("text/csv, application/json")
    Response shortEvents() {
        def conf = this.conference
        Response.ok()
                .entity(conference.events.collect { e ->
                    new ShortEvent(e, conf.id, 'de')
                })
                .header('Content-Disposition', /attachment; filename="${conference.getId()}.csv"/)
                .build()
    }

    @GET
    @Path("speakers")
    @ApiOperation(value = "returns list of conference speakers",
            response = Speaker.class,
            responseContainer = "List")
    public Response getSpeakers() {
        return Response.ok().entity(conference.speakers).build();
    }

    @GET
    @Path("events")
    @ApiOperation(value = "returns list of conference events",
            response = Event.class,
            responseContainer = "List")
    public Response getEvents() {
        return Response.ok().entity(conference.events).build();
    }

    @GET
    @Path("metadata")
    @ApiOperation(value = "returns list of conference meta data",
            response = MetaData.class)
    public Response getMeta() {
        return Response.ok().entity(conference.metaData).build();
    }
}
