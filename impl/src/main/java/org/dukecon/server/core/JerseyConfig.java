package org.dukecon.server.core;

import javax.ws.rs.ApplicationPath;

import org.dukecon.server.conference.ConferencesResource;
import org.dukecon.server.filter.FiltersService;
import org.dukecon.server.security.KeycloakConfigService;
import org.dukecon.server.preferences.NoAuthPreferencesServices;
import org.dukecon.server.preferences.PreferencesService;
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
        register(ConferencesResource.class);
        register(MetaService.class);
        register(KeycloakConfigService.class);
        register(TalkService.class);
        register(PreferencesService.class);
        register(NoAuthPreferencesServices.class);
        register(FiltersService.class);
    }

}