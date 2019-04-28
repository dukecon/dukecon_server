package org.dukecon.server.favorites

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

import javax.ws.rs.Path

/**
 * @author Gerd Aschemann <gerd@aschemann.net>
 */
@Component
@Path("/noauthpreferences")
// @Api(value = "/noauthpreferences", description = "Conditional unauthorized preferences endpoint")
@Slf4j
@TypeChecked
@ConditionalOnProperty(name="preferences.noauth.enable", havingValue="true", matchIfMissing = false)
class NoAuthPreferencesServiceImpl extends AbstractPreferencesService {

    @Value ("\${preferences.noauth.principalId:dummyPrincipal}")
    String principalId

    protected String getAuthenticatedPrincipalId () {
        log.warn ("Unauthorized access to preferences: IT IS STRONGLY RECOMMENDED NOT TO ENABLE THIS FOR PRODUCTION ENVIRONMENTS")

        return principalId
    }
}
