package org.dukecon.server.conference

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.codehaus.groovy.runtime.NioGroovyMethods
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.core.env.PropertySource
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.nio.charset.StandardCharsets
import java.nio.file.Path
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
    Map<String, Object> configurationProperties = [:]

    private boolean readConferences = true

    @Inject
    ConferencesConfigurationServiceImpl(ConfigurableEnvironment env) {
        this.env = env
        this.readConferences = !env.getActiveProfiles().contains("noconferences")
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
        this.configurationProperties = getAllKnownConfigurationProperties(env)
        conferencesConfigurationFile = configurationProperties["conferences.file"] ?: "conferences-dev.yml"
        if (readConferences) {
            log.info("Loading conferences enabled, run application with profile 'noconferences' to disable!");
            log.debug("Loading conferences file '{}'", conferencesConfigurationFile)
            configuration.conferences.addAll(
                    ConferencesConfiguration.fromFile(conferencesConfigurationFile,
                            configurationProperties)?.conferences)
        } else {
            log.info("Loading conferences disabled, run application without profile 'noconferences' to enable!");
        }
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

    String getBackupDir() {
        return configurationProperties['backup.dir']
    }

    @Override
    void reloadInputFile(Path file) {
        if (!readConferences) {
            // TODO implement reloading of cached eventsData.json
            log.info("""Reloading event data from '{}':
{}""", file.toAbsolutePath(), NioGroovyMethods.getText(file, StandardCharsets.ISO_8859_1.name()))
        }
    }
}
