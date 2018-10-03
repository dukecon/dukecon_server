package org.dukecon.server.conference.adpater.heise

import org.dukecon.model.Audience
import org.dukecon.model.Conference
import org.dukecon.server.repositories.RawDataResources
import org.dukecon.server.repositories.heise.HeiseAudienceMapper
import org.dukecon.server.repositories.heise.HeiseCsvInput
import org.dukecon.server.repositories.heise.HeiseDataExtractor
import org.dukecon.server.repositories.heise.HeiseEventMapper
import org.dukecon.server.repositories.heise.HeiseEventTypeMapper
import org.dukecon.server.repositories.heise.HeiseLanguageMapper
import org.dukecon.server.repositories.heise.HeiseLocationMapper
import org.dukecon.server.repositories.heise.HeiseSpeakerMapper
import org.dukecon.server.repositories.heise.HeiseStreamMapper
import org.dukecon.server.conference.SpeakerImageService
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack.
 */
class HeiseDataExtractorSpec extends Specification {
    private static HeiseDataExtractor extractor = new HeiseDataExtractor('hc16', readCsv(), LocalDate.parse('2016-08-30', DateTimeFormatter.ofPattern("yyyy-MM-dd")))
    private Conference conference

    void setup() {
        conference = extractor.conference
    }

    private static readCsv() {
        new HeiseCsvInput(RawDataResources.of('herbstcampus-2016/herbstcampus_2016_veranstaltungen_20160826.csv'))
    }

    void "should contain metadata"() {
        expect:
        conference.id == 'hc16'
        conference.name == 'DukeCon Conference'
        conference.url == 'http://dukecon.org'
        conference.metaData
    }

    void "should get 12 streams"() {
        when:
        def streams = new HeiseStreamMapper(readCsv()).entities
        then:
        streams.size() == 12
        streams.names['de'] == ['Java', '.NET', 'JavaScript', 'andere Sprachen', 'Architektur', 'Testen/Qualität', 'Infrastruktur', 'Sicherheit', 'Agile/Soft Skills', 'Mobile', 'Big Data/Search', 'Diverses']
    }

    // TODO Rebuild without SpeakerImageService
//    void "should read 51 speakers"() {
//        when:
//        def mapper = new HeiseSpeakerMapper(readCsv(), new SpeakerImageService())
//        def speakers = mapper.speakers
//        then:
//        speakers.size() == 51
//        mapper.eventIdsToSpeaker.get('5511').size() == 2
//        mapper.eventIdsToSpeaker.get('5287').first().is(mapper.eventIdsToSpeaker.get('5290').first())
//        speakers.events*.size().each { it > 0 }
//    }

    // TODO Rebuild without SpeakerImageService
//    void "should read 51 talks"() {
//        given:
//        def mapper = new HeiseEventMapper(readCsv(), LocalDate.parse('2016-08-30', DateTimeFormatter.ofPattern("yyyy-MM-dd")), new HeiseSpeakerMapper(readCsv(), new SpeakerImageService()), new HeiseLanguageMapper(readCsv()), new HeiseStreamMapper(readCsv()), new HeiseAudienceMapper(readCsv()), new HeiseEventTypeMapper(readCsv()), new HeiseLocationMapper(readCsv()))
//
//        when:
//        def events = mapper.events
//
//        then:
//        events.size() == 51
//        events.speakers*.size().count(1) == 43
//        events.speakers*.size().count(2) == 8
//        events.speakers*.size().count(3) == 0
//
//        when:
//        def event = events.find { it.id == '5287' }
//
//        then:
//        event.speakers.first().name == 'Falk Sippach'
//        event.speakers.first().events.size() == 2
//        event.id == '5287'
//        event.title == 'Kontinuierlich und effizient - Agil Softwarearchitektur dokumentieren'
//        event.start == LocalDateTime.of(2016, 8, 31, 15, 40)
//        event.end == LocalDateTime.of(2016, 8, 31, 16, 50)
//        event.type.names.de == 'Vortrag'
//        event.language.code == 'de'
//        event.track.names['de'] == 'JavaScript'
//
//        when:
//        event = events.find { it.id == '5140' }
//
//        then:
//        event.speakers.first().name == 'Stefan Lieser'
//        event.type.names.de == 'Tutorium'
//        event.start == LocalDateTime.of(2016, 8, 30, 9, 40)
//        event.end == LocalDateTime.of(2016, 8, 30, 18, 30)
//    }

    void "should get one language"() {
        given:
        def mapper = new HeiseLanguageMapper(readCsv())

        when:
        def languages = mapper.languages
        def defaultLanguage = mapper.defaultLanguage

        then:
        languages.size() == 1
        languages.names['de'] == ['Deutsch']
        defaultLanguage.names['de'] == 'Deutsch'
    }

    void "should get 6 locations"() {
        when:
        def locations = new HeiseLocationMapper(readCsv()).entities

        then:
        locations.size() == 6
        locations.names['de'] == ['H1', 'H2', 'H3', 'H4', 'H5', 'XX']
    }

    void "should get 3 event types"() {
        given:
        def mapper = new HeiseEventTypeMapper(readCsv())

        when:
        def eventTypes = mapper.entities
        def eventType = mapper.entityForName('Keynote')

        then:
        eventTypes.size() == 3
        eventTypes.names['de'] == ['Keynote', 'Tutorium', 'Vortrag']
        eventType.id == '1'
        eventType.icon == 'eventType_1.png'
    }

    void "should get 3 audiences"() {
        given:
        def mapper = new HeiseAudienceMapper(readCsv())

        when:
        def audiences = mapper.entities
        def audience = mapper.entityForName("Einsteiger")

        then:
        audiences.size() == 2
        audiences.names['de'] == ['Einsteiger', 'Experten']
        audience instanceof Audience
        audience.id == '1'
        audience.icon == 'audience_1.png'
    }


}
