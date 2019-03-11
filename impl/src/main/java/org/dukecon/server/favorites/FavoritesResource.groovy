package org.dukecon.server.favorites

import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.dukecon.services.ConferenceService
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
@Path("favorites/{conferenceId}")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class FavoritesResource {

    private ConferenceService conferenceService
    private FavoritesService favoritesService

    @Inject
    FavoritesResource(ConferenceService conferenceService, FavoritesService favoritesService) {
        this.conferenceService = conferenceService
        this.favoritesService = favoritesService
    }

    /**
     * Renders favorites grouped by event per conference as CSV or alternatively as JSON data.
     * @param id the conference id
     * @return http response with csv or json content
     */
    @GET
    @Produces("text/csv, application/json")
    Response getFavorites(@PathParam("conferenceId") String id) {
        def conference = conferenceService.getConference(id)

        def events = favoritesService.getAllFavoritesForConference(conference)

        Response.ok().entity(events).header("Content-Disposition", "attachment; filename=${getCsvFileName(id)}").build()
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private String getCsvFileName(String id) {
        "favorites-${id}-${new Date().format('yyyy-MM-dd')}.csv"
    }
}
