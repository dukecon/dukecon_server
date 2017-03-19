package org.dukecon.server.conference

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.time.format.DateTimeFormatter

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class CurrentConferenceResource {
    private final ConferencesConfigurationService configurationService
    private final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE

    @Value("\${conferences.default.shortname:javaland}")
    String defaultConferenceName

    @Value("\${conferences.default.year:2017}")
    String defaultConferenceYear

    @Inject
    CurrentConferenceResource(ConferencesConfigurationService configurationService) {
        this.configurationService = configurationService
    }

    @GET
    @Path("init.json")
    public Response defaultConferenceForLocalDevelopment() {
        return getCurrentConference(defaultConferenceName, defaultConferenceYear)
    }

    @GET
    // TODO: Second identifier might be more generic, i.e., different from "year" connotation
    @Path("init/{conference:[a-zA-Z_0-9]+}/{year:[0-9]+}")
    public Response getCurrentConference(@PathParam("conference") String conference, @PathParam("year") String year) {
        def c = configurationService.getConference(conference, year)
        if (c) {
            return Response.ok().entity(c ? [id: c.id, name: c.name, year: c.year, url: c.url, homeUrl: c.homeUrl, homeTitle: c.homeTitle, startDate: dtf.format(c.startDate), endDate: dtf.format(c.endDate)] : [:]).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
