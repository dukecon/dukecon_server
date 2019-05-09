package org.dukecon.server.formes2dukecon

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.dukecon.server.conference.ConferencesConfiguration
import org.dukecon.server.conference.SpeakerImageService
import org.dukecon.server.repositories.ConferenceDataExtractor
import org.dukecon.server.repositories.RawDataMapper
import org.dukecon.server.repositories.RawDataResources

import java.time.format.DateTimeFormatter


@CompileStatic
@Slf4j
class FormesToDukecon {

    private static void usage(String msg = null, int exitCode = 0) {
        if (msg) {
            log.error("Error calling '{}': {}", FormesToDukecon.class, msg)
        }
        log.info("Usage: {} <conferenceUrl>", FormesToDukecon.class)
        if (exitCode) {
            System.exit(exitCode)
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage("Wrong number of arguments", 1)
        }
        ConferencesConfiguration conferencesConfiguration = ConferencesConfiguration.fromFile(args[0], [:], false)
        conferencesConfiguration.conferences.each { ConferencesConfiguration.Conference conferenceConfig ->
            RawDataResources rawDataResources = RawDataResources.of(conferenceConfig)
            Class rawDataMapperClass = conferenceConfig.rawDataMapperClass as Class
            RawDataMapper rawDataMapper =
                    rawDataMapperClass.getConstructor(RawDataResources.class)
                            .newInstance(rawDataResources)
            rawDataMapper.initMapper()
            Class conferenceDataExtractorClass = conferenceConfig.extractorClass as Class
            ConferenceDataExtractor conferenceDataExtractor =
                    conferenceDataExtractorClass.getConstructor(ConferencesConfiguration.Conference.class,
                            RawDataMapper.class, SpeakerImageService.class)
                            .newInstance(conferenceConfig, rawDataMapper, new DummySpeakerImageService())
            Conference conference = conferenceDataExtractor.conference
            ObjectMapper objectMapper = new ObjectMapper()
            File conferenceJson = new File("static/${conferenceConfig.conference}/${conferenceConfig.year}/${conferenceConfig.id}.json")
            conferenceJson.getParentFile().mkdirs()
            objectMapper.writeValue(conferenceJson, conference)
            log.info("Created {}", conferenceJson.absolutePath)

            File initJson = new File("static/${conferenceConfig.conference}/${conferenceConfig.year}/init.json")
            objectMapper.writeValue(initJson, getInitJsonContent(conferenceConfig))
            log.info("Created {}", initJson.absolutePath)
        }
    }

    private static Map<String, Object> getInitJsonContent(ConferencesConfiguration.Conference c) {
        final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE
        [
                id          : c.id,
                name        : c.name,
                year        : c.year,
                url         : c.url,
                homeUrl     : c.homeUrl,
                homeTitle   : c.homeTitle,
                imprint     : [
                        de      : c.imprint.de,
                        en      : c.imprint.en
                ],
                termsOfUse  : c.termsOfUse,
                privacy     : c.privacy,
                startDate   : dtf.format(c.startDate),
                endDate     : dtf.format(c.endDate),
                authEnabled : c.authEnabled,
                admin       : "../rest/admin/${c.id}".toString(),
                forceUpdate : "../rest/conferences/update/${c.id}".toString(),
                conferences : "../rest/conferences/${c.id}".toString(),
                events      : "../rest/eventsBooking/${c.id}".toString(),
                keycloak    : "../rest/keycloak.json",
                feedbackServer: [
                        active: Boolean.valueOf(c.feedbackServer.active),
                        timeSlotVisible: c.feedbackServer.timeSlotVisible as int
                ]
        ]
    }
}
