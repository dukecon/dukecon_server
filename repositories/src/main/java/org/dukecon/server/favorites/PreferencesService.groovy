package org.dukecon.server.favorites

import javax.ws.rs.Path
import javax.xml.ws.Response

/**
 * Created by ascheman on 17.06.17.
 */
@Path("preferences")
interface PreferencesService {
    public Response getPreferences()
}