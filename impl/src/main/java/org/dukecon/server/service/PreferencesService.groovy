package org.dukecon.server.service

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * @author Gerd Aschemann <gerd@aschemann.net>
 */
@Component
@Path("preferences")
class PreferencesService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public @ResponseBody String preferences() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return "Hello " + auth?.principal.toString() ?: "Stranger"
    }
}
