package org.dukecon.server.core

import groovy.util.logging.Slf4j

import org.springframework.boot.web.server.MimeMappings
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.stereotype.Component

@Component
@Slf4j
public class ServletCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
		log.debug ("Adding manifest mime mapping!")
        MimeMappings mappings = factory.mimeMappings
        mappings.add("manifest","text/cache-manifest")
        factory.setMimeMappings(mappings)
    }
}