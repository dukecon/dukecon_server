package org.dukecon.server.security

import groovy.util.logging.Slf4j

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * @author Gerd Aschemann, gerd@aschemann.net, @GerdAschemann
 */
@Component
@Path("keycloak.json")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
class KeycloakConfigService {
	@Value("\${keycloak.realm}")
	private String realm

	@Value("\${keycloak.auth-server-url}")
	private String authServerUrl
	
	@Value("\${keycloak.ssl-required:none}")
	private String sslRequired
	
	@Value("\${keycloak.resource:dukecon}")
	private String resource

	@Value("\${keycloak.useAccountManagement:false}")
    private boolean useAccountManagement
	
	// TODO: Check if we could parameterize this?
	@Value("\${preferences.rest.path}")
	private String redirectUri

	@GET
	Response getKeycloakConfig () {
        Map<String, String> keyCloakConfig = [
			'realm': realm,
			'auth-server-url': authServerUrl,
			'ssl-required':	sslRequired,
			'resource': resource,
			'redirectUri': redirectUri,
			// Use toString() since JSON serializer otherwise makes a GString representation out ouf it!
            'useAccountManagement': useAccountManagement.toString()
        ]
		log.debug ("keycloak.json = '{}'", keyCloakConfig)
		
        return Response.ok().entity(keyCloakConfig).build()
	}

}
