package org.dukecon.server.formes2dukecon

import com.fasterxml.jackson.databind.ObjectMapper
import org.dukecon.model.Conference
import org.dukecon.server.conference.ConferencesConfiguration
import org.dukecon.server.conference.SpeakerImageService
import org.dukecon.server.repositories.ConferenceDataExtractor
import org.dukecon.server.repositories.RawDataMapper
import org.dukecon.server.repositories.RawDataResources

class FormesToDukecon {
    public static void main(String[] args) {
        ConferencesConfiguration conferencesConfiguration = ConferencesConfiguration.fromFile(args[0], [:], false)
        conferencesConfiguration.conferences.each { ConferencesConfiguration.Conference conferenceConfig ->
            RawDataResources rawDataResources = new RawDataResources([eventsData: conferenceConfig.talksUri
                    .eventsData])
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
