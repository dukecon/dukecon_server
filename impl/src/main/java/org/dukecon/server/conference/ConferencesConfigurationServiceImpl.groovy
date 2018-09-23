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
class ConferencesConfigurationServiceImpl implements ConferencesConfigurationService {
    private final ConferencesConfiguration configuration = new ConferencesConfiguration()
    private final ConfigurableEnvironment env
    private String conferencesConfigurationFile

    @Inject
    ConferencesConfigurationServiceImpl(ConfigurableEnvironment env) {
        this.env = env
    }

    // TODO Clean up - it is called twice!
    // looks like, it is called the 2nd time because of @PostConstruct. Unfortunately contains
    // configurationProperties["conferences.file"] different values between the both calls, one time from the
    // profile settings and the second time from a local application.properties
    // it is called twice because of first manually instantiation in DukeConServerApplication and as @Service annotated
    // class, we need @Service for the spring container to manage an (other) instance of this class
    @PostConstruct
    @Override
    void init() {
        Map<String, Object> configurationProperties = getAllKnownConfigurationProperties(env)
        conferencesConfigurationFile = configurationProperties["conferences.file"] ?: "conferences-dev.yml"
        log.debug ("Loading conferences file '{}'", conferencesConfigurationFile)
        configuration.conferences.addAll(
                ConferencesConfiguration.fromFile(conferencesConfigurationFile,
                        configurationProperties)?.conferences)
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

    @Override
    List<ConferencesConfiguration.Conference> getConferences() {
        configuration.conferences
    }

    @Override
    ConferencesConfiguration.Conference getConference(String conference, String year) {
        configuration.conferences.find { ConferencesConfiguration.Conference config ->
            config.conference == conference && config.year == year
        }
    }

    @Override
    ConferencesConfiguration.Conference getConference(String conferenceId) {
        configuration.conferences.find { ConferencesConfiguration.Conference config ->
            config.id == conferenceId
        }
    }
}
