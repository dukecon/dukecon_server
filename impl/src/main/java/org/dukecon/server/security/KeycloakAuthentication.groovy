package org.dukecon.server.security

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import java.security.Principal

import javax.servlet.http.HttpServletRequest

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

/**
 * Wrapper for Spring authentication for keycloak data
 * 
 * Taken from https://github.com/iuliazidaru/keycloak-spring-boot-rest-angular-demo
 * 
 * @author iulia
 */
@Slf4j
@TypeChecked
public class KeycloakAuthentication implements Authentication {
	private boolean authenticated
	private Principal principal


	public KeycloakAuthentication(Principal principal) {
		super()
		this.authenticated = true
		this.principal = principal
	}

	public KeycloakAuthentication(boolean authenticated) {
		super()
		this.authenticated = authenticated
	}



	@Override
	public Object getCredentials() {
		return null
	}

	@Override
	public Object getDetails() {
		return null
	}

	@Override
	public Object getPrincipal() {
		return principal
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		this.authenticated = isAuthenticated
	}

	public static class KeycloakAuthenticationBuilder {

		public static KeycloakAuthentication buildAuthentication(HttpServletRequest request) {
			Principal principal = request?.userPrincipal
			if(principal){
				log.debug ("New session for '{}'", principal)
				return new KeycloakAuthentication(principal)
			} else {
				return new KeycloakAuthentication(false)
			}
		}
	}

	@Override
	public String getName() {
		return principal?.name
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null
	}
}
