package org.dukecon.server.core;

import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The Apache web server we run in front of this application compresses the responses with gzip. It also modifies the etag.
 * This is a workaround to strip the additional etag from the header when the clients sends a request for this resource.
 *
 * @author Alexander Schwartz 2017
 */
public class MyShallowEtagHeaderFilter extends ShallowEtagHeaderFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                String value = super.getHeader(name);
                if (name.equalsIgnoreCase("If-None-Match") && value != null) {
                    value = value.replaceAll("-gzip", "");
                }
                return value;
            }
        };
        super.doFilterInternal(wrapper, response, filterChain);
    }
}
