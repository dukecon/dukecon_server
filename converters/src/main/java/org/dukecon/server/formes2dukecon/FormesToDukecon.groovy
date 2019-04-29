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
            File json = new File("conference-${conferenceConfig.id}.json")
            objectMapper.writeValue(json, conference);
            println json.absolutePath
        }
    }
}
