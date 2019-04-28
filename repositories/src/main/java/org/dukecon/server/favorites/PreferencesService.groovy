package org.dukecon.server.favorites

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by ascheman on 17.06.17.
 */
@Path("/preferences")
// TODO Move this to "impl" or even "api" and add @Api for OpenAPI
public interface PreferencesService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPreferences()

    /**
     * Retrieves all events with number of favored by users.
     * @return map with event id as key and number of favored by users
     */
    Map<String, Integer> getAllEventFavorites()
}
