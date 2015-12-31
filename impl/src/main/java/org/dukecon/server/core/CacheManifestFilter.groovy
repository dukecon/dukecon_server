package org.dukecon.server.core

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.stereotype.Component

@Component
@Slf4j
@TypeChecked
public class CacheManifestFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req
		String reqUrl = request.getRequestURL().toString()
		HttpServletResponse response = (HttpServletResponse) res
		if (reqUrl.endsWith("cache.manifest")) {
			log.debug ("Adding no-cache control to manifest file '{}'", reqUrl)
			response.setHeader("Cache-Control", "no-cache, max-age=0, no-store")
			response.setHeader("Pragma", "no-cache")
		} else {
			log.debug ("Not adding cache control to '{}'", reqUrl)
		}
		chain.doFilter(req, res);
	}

	public void init(FilterConfig filterConfig) {}

	public void destroy() {}

}
