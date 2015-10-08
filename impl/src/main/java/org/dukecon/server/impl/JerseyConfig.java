package org.dukecon.server.impl;

import javax.ws.rs.ApplicationPath;

import org.dukecon.server.service.*;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * @author ascheman
 *
 */
@Component
@ApplicationPath ("/rest")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(ConferenceService.class);
        register(MetaService.class);
        register(TalkService.class);
        register(PreferencesService.class);
        register(NoAuthPreferencesServices.class);
        register(FiltersService.class);
    }

}