package org.dukecon.server.core

import groovy.util.logging.Slf4j

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer
import org.springframework.boot.context.embedded.MimeMappings
import org.springframework.stereotype.Component

@Component
@Slf4j
public class ServletCustomizer implements EmbeddedServletContainerCustomizer {

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
		log.debug ("Adding manifest mime mapping!")
        MimeMappings mappings = container.mimeMappings
        mappings.add("manifest","text/cache-manifest")
        container.setMimeMappings(mappings)
    }
}