package org.dukecon.server.conference

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.CoreImages
import org.dukecon.services.ResourceService
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
class CurrentConferenceResourceConstants {
    // TODO: Second identifier might be more generic, i.e., different from "year" connotation
    static final String URI_TEMPLATE = "{conference:[a-zA-Z_0-9]+}/{year:[0-9]+}"
    static final String INIT_TEMPLATE = "init/{conference:[a-zA-Z_0-9]+}/{year:[0-9]+}"
    static final String IMAGE_RESOURCES_TEMPLATE = "image-resources/{conference:[a-zA-Z_0-9]+}/{year:[0-9]+}"
}

@Component
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class CurrentConferenceResource {
    private final ConferencesConfigurationService configurationService
    private final ResourceService resourceService
    private final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE

    @Value("\${conferences.default.shortname:javaland}")
    String defaultConferenceName

    @Value("\${conferences.default.year:2017}")
    String defaultConferenceYear

    @Inject
    CurrentConferenceResource(ConferencesConfigurationService configurationService, ResourceService resourceService) {
        this.configurationService = configurationService
        this.resourceService = resourceService
    }

    @GET
    @Path("init.json")
    public Response defaultConferenceForLocalDevelopment() {
        return getCurrentConference(defaultConferenceName, defaultConferenceYear)
    }

    @GET
    @Path(CurrentConferenceResourceConstants.INIT_TEMPLATE)
    public Response getCurrentConference(@PathParam("conference") String conference, @PathParam("year") String year) {
        def c = configurationService.getConference(conference, year)
        if (c) {
            return Response.ok().entity(c ? [
                    id          : c.id,
                    name        : c.name,
                    year        : c.year,
                    url         : c.url,
                    homeUrl     : c.homeUrl,
                    homeTitle   : c.homeTitle,
                    imprint     : [
                        de      : c.imprint.de,
                        en      : c.imprint.en
                    ],
                    termsOfUse  : c.termsOfUse,
                    privacy     : c.privacy,
                    startDate   : dtf.format(c.startDate),
                    endDate     : dtf.format(c.endDate),
                    authEnabled : c.authEnabled,
                    admin       : "../rest/admin/${c.id}".toString(),
                    conferences : "../rest/conferences/${c.id}".toString(),
                    events      : "../rest/eventsBooking/${c.id}".toString(),
                    keycloak    : "../rest/keycloak.json",
            ] : [:]).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("image-resources.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response defaultImageResourcesForLocalDevelopment() {
        return getCurrentImageResources(defaultConferenceName, defaultConferenceYear)
    }

    @GET
    @Path(CurrentConferenceResourceConstants.IMAGE_RESOURCES_TEMPLATE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentImageResources(
            @PathParam("conference") String conference, @PathParam("year") String year) {
        ConferencesConfiguration.Conference c = configurationService.getConference(conference, year)
        if (c) {
            CoreImages i = resourceService.getCoreImagesForConference(c.id)
            if (!i) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            Response.ResponseBuilder b = Response.ok().entity(i);
            Response r = b.build();
            return r;
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }
}