package org.dukecon.server.security;

import javax.servlet.*
import javax.servlet.http.HttpServletRequest

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * Taken from https://github.com/iuliazidaru/keycloak-spring-boot-rest-angular-demo
 * 
 * @author iulia
 */
@Component
public class AuthenticationFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		//we must obtain the user from the context.
		try{
			SecurityContextHolder.getContext().setAuthentication(
					KeycloakAuthentication.KeycloakAuthenticationBuilder.buildAuthentication((HttpServletRequest)request));
			chain.doFilter(request, response)
		} finally {
			SecurityContextHolder.clearContext()
		}
	}
	
	@Override
	public void destroy() {
	}

}
