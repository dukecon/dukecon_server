package org.dukecon.server.conference

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.core.env.PropertySource
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
@Slf4j
@Service
class ConferencesConfigurationService {
    private final ConferencesConfiguration configuration = new ConferencesConfiguration()
    private final ConfigurableEnvironment env

    @Inject
    ConferencesConfigurationService(ConfigurableEnvironment env) {
        this.env = env
    }

    @PostConstruct
    void init() {
        configuration.conferences.addAll(ConferencesConfiguration.fromFile('conferences.yml', getAllKnownConfigurationProperties(env))?.conferences)
    }

    private static Map<String, Object> getAllKnownConfigurationProperties(ConfigurableEnvironment env) {
        Map<String, Object> result = [:]
        env.propertySources.each { PropertySource ps ->
            if (ps instanceof EnumerablePropertySource) {
                ((EnumerablePropertySource) ps).propertyNames.each { String name ->
                    result[name] = ps.getProperty(name)
                }
            }
        }
        result
    }

    List<ConferencesConfiguration.Conference> getConferences() {
        configuration.conferences
    }

    ConferencesConfiguration.Conference getConference(String conference, String year) {
        configuration.conferences.find { ConferencesConfiguration.Conference config ->
            config.conference == conference && config.year == year
        }
    }
}
