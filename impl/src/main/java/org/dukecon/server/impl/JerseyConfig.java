package org.dukecon.server.impl;

import org.dukecon.server.service.MetaService;
import org.dukecon.server.service.TalkService;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * @author ascheman
 *
 */
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(MetaService.class);
        register(TalkService.class);
    }

}