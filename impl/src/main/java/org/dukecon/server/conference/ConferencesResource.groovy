package org.dukecon.server.conference

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import io.swagger.annotations.*

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@Path("conferences")
@Api(value="/", description = "Conferences endpoint")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class ConferencesResource {
    List<ConferenceDataProvider> talkProviders

    @Inject
    ConferencesResource(List<ConferenceDataProvider> talkProviders) {
        this.talkProviders = talkProviders
    }

    @GET
    @ApiOperation(value="returns list of conferences",
            response = Conference.class,
            responseContainer = "List")
    public Response getConferences() {
        def conferences = talkProviders.collect{p -> [id : p.conference.id, name : p.conference.name]}
        return Response.ok().entity(conferences).build()
    }

    @GET
    @Path("update/{id:[0-9]*}")
    public Response updateConference(@PathParam("id") String id) {
        try {
            def provider = getConferenceProvider(id);
            if (provider == null)
                return Response.status(Response.Status.NOT_FOUND).build()
            if (provider.update()) {
                return Response.ok().entity([message: "ok"]).build()
            }
            if (provider.isBackupActive()) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity([message: "backup active"]).build()
            }
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity([message: provider.staleException.toString()]).build()
        } catch (RuntimeException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity([message: e.toString()]).build()
        }
    }

    @Path("{id}")
    @ApiOperation(value = "Conference details")
    public ConferenceDetailResource getConferenceDetails(@PathParam("id") String id) {
        def provider = getConferenceProvider(id.replace(".json", ""))
        if (provider == null) {
            log.warn("Conference with id {} not found", id)
            return new ConferenceDetailResource(null)
        }
        return new ConferenceDetailResource(provider.conference)
    }

    private ConferenceDataProvider getConferenceProvider(String id) {
        return talkProviders.find{p -> p.conference.id == id}
    }
}
