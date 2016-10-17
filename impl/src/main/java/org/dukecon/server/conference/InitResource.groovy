package org.dukecon.server.conference

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.dukecon.model.Conference
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
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class InitResource {
    List<ConferenceDataProvider> talkProviders

    @Inject
    InitResource(List<ConferenceDataProvider> talkProviders) {
        this.talkProviders = talkProviders
    }

    @GET
    // TODO: Second identifier might be more generic, i.e., different from "year" connotation
    @Path("init/{conference:[a-zA-Z_0-9]+}/{year:[0-9]+}")
    public Response getCurrentConference(@PathParam("conference") String conference, @PathParam("year") String year) {
        def c = talkProviders.conference.find{it?.name ==~ /.*(?i)${conference}.*/ && it?.name ==~ /.*${year}.*/}
        return Response.ok().entity(c ? [id: c.id, name: c.name] : [:]).build();
    }
}
