package org.dukecon.server.impl;

import javax.ws.rs.ApplicationPath;

import org.dukecon.server.service.ConferenceService;
import org.dukecon.server.service.FiltersService;
import org.dukecon.server.service.KeycloakConfigService;
import org.dukecon.server.service.MetaService;
import org.dukecon.server.service.NoAuthPreferencesServices;
import org.dukecon.server.service.PreferencesService;
import org.dukecon.server.service.TalkService;
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
        register(KeycloakConfigService.class);
        register(TalkService.class);
        register(PreferencesService.class);
        register(NoAuthPreferencesServices.class);
        register(FiltersService.class);
    }

}