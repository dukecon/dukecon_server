package org.dukecon.server.core;

import javax.ws.rs.ApplicationPath;

import org.dukecon.server.admin.AdminResource;
import org.dukecon.server.conference.ConferencesResource;
import org.dukecon.server.conference.CurrentConferenceResource;
import org.dukecon.server.conference.EventBookingResource;
import org.dukecon.server.speaker.SpeakerImageResource;
import org.dukecon.server.filter.FiltersService;
import org.dukecon.server.security.KeycloakConfigService;
import org.dukecon.server.favorites.NoAuthPreferencesService;
import org.dukecon.server.favorites.PreferencesService;
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
        register(CurrentConferenceResource.class);
        register(MetaService.class);
        register(KeycloakConfigService.class);
        register(PreferencesService.class);
        register(NoAuthPreferencesService.class);
        register(FiltersService.class);
        register(SpeakerImageResource.class);
        register(AdminResource.class);
        register(EventBookingResource.class);

        register(CorsFilter.class);
    }

}