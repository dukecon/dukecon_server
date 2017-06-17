package org.dukecon.server.favorites

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

import javax.ws.rs.Path

/**
 * @author Gerd Aschemann <gerd@aschemann.net>
 */
@Component
@Slf4j
@TypeChecked
@ConditionalOnProperty(name="preferences.noauth.enable", havingValue="false", matchIfMissing = true)
class PreferencesServiceImpl extends AbstractPreferencesService {
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
