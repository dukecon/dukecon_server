package org.dukecon.server.favorites

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.ws.rs.Path

/**
 * @author Gerd Aschemann <gerd@aschemann.net>
 */
@Component
@Path("noauthpreferences")
@Slf4j
@TypeChecked
class NoAuthPreferencesServiceImpl extends AbstractPreferencesService {
    @Value ("\${preferencess.noauth.enable:false}")
    boolean noauthEnabled

    @Value ("\${preferencess.noauth.principalId:dummyPrincipal}")
    String principalId

    protected String getAuthenticatedPrincipalId () {
        if (!noauthEnabled) {
            log.error ("Unauthenticated access to preferences!")
            return null
        }
        log.warn ("Unauthorized access to preferences: IT IS STRONGLY RECOMMENDED NOT TO ENABLE THIS FOR PRODUCTION ENVIRONMENTS")

        return principalId
    }
}
