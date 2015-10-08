package org.dukecon.server.service

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.user.UserFilters
import org.dukecon.server.model.Filters
import org.dukecon.server.repository.FiltersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@Path("filters")
@Slf4j
@TypeChecked
@Transactional
class FiltersService {

    @Autowired
    private FiltersRepository repository

    private String getAuthenticatedPrincipalId() {
        String principalId = SecurityContextHolder.getContext().getAuthentication()?.principal?.toString()
        if (!principalId) {
            log.error("There is no authentication given, the authentication '{}' does not contain a valid principal, using constant fallback value", SecurityContextHolder.getContext().getAuthentication())
            return "NO_PRINCIPAL_FOUND"
        }
        return principalId
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveFilter(UserFilters userFilters) {
        log.debug("Saving filters for '{}'", authenticatedPrincipalId)
        Filters filters = repository.findByPrincipalId(authenticatedPrincipalId)
        def responseStatus = Response.Status.OK
        if (!filters) {
            filters = repository.save(new Filters(principalId: authenticatedPrincipalId))
            responseStatus = Response.Status.CREATED
        }
        filters.favourites = userFilters.favourites
        filters.tracks = userFilters.tracks
        filters.rooms = userFilters.rooms
        filters.languages = userFilters.languages
        filters.levels = userFilters.levels
        return Response.status(responseStatus).build()
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFilters() {
        if (!authenticatedPrincipalId) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        log.debug("Retrieving filters for '{}'", authenticatedPrincipalId)
        Filters filters = repository.findByPrincipalId(authenticatedPrincipalId)

        def result = filters ? UserFilters.builder().favourites(filters.favourites).rooms(filters.rooms).tracks(filters.tracks).languages(filters.languages).levels(filters.levels).build() : new UserFilters()

        return Response.ok(result).build()
    }
}