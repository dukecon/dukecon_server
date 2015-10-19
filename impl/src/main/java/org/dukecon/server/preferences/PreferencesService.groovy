package org.dukecon.server.preferences

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

import javax.ws.rs.Path

/**
 * @author Gerd Aschemann <gerd@aschemann.net>
 */
@Component
@Path("preferences")
@Slf4j
@TypeChecked
class PreferencesService extends AbstractPreferencesService {
    protected String getAuthenticatedPrincipalId () {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth) {
            log.error ("There is no authentication given")
            return null
        }

        Object principal = auth.principal
        if (!principal) {
            log.error ("The authentication '{}' does not contain a valid principal", auth)
            return null
        }
        String principalId = principal.toString()

        return principalId
    }
}
