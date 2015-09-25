package org.dukecon.server.business

import groovy.json.JsonSlurper
import org.dukecon.model.Audience
import org.dukecon.model.Conference
import org.dukecon.model.Language
import org.dukecon.model.MetaData
import org.dukecon.model.Room
import spock.lang.Specification


/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class MetaDataExtractorSpec extends Specification {

    private static MetaDataExtractor extractor

    void setupSpec() {
        InputStream is = MetaDataExtractorSpec.class.getResourceAsStream('/javaland-2016.raw')
        JsonSlurper slurper = new JsonSlurper()
        def rawJson = slurper.parse(is, "ISO-8859-1")
        extractor = new MetaDataExtractor(talksJson: rawJson.hits.hits._source)
    }

    void "should get 8 tracks"(){
        when:
        def tracks = extractor.tracks
        then:
        assert tracks.size() == 8

        assert tracks[0].names["de"] == "Container & Microservices"
        assert tracks[0].order == 1

        assert tracks[1].names["de"] == "Core Java & JVM basierte Sprachen"
        assert tracks[1].order == 2

        assert tracks[2].names["de"] == "Enterprise Java & Cloud"
        assert tracks[2].order == 3

        assert tracks[3].names["de"] == "Frontend & Mobile"
        assert tracks[3].order == 4

        assert tracks[4].names["de"] == "IDEs & Tools"
        assert tracks[4].order == 5

        assert tracks[5].names["de"] == "Internet der Dinge"
        assert tracks[5].order == 6

        assert tracks[6].names["de"] == "Architektur & Sicherheit"
        assert tracks[6].order == 7

        assert tracks[7].names["de"] == "Newcomer"
        assert tracks[7].order == 8
    }

    void "should default language be 'de'"() {
        when:
        Language language = extractor.defaultLanguage
        then:
        assert language.code == 'de'
        assert language.name == 'Deutsch'
    }
    void "should list languages"() {
        when:
        List<Language> languages = extractor.languages
        then:
        assert languages.size() == 2
        assert languages[0].code == 'de'
        assert languages[0].name == 'Deutsch'
        assert languages[1].code == 'en'
        assert languages[1].name == 'English'
    }

    void "should list two audience levels"() {
        when:
        List<Audience> audiences = extractor.audiences
        then:
        assert audiences.size() == 2
        assert audiences[0].order == 1
        assert audiences[0].names.de == 'Anfänger'
        assert audiences[0].names.en == 'beginners'
        assert audiences[1].order == 2
        assert audiences[1].names.de == 'Fortgeschrittene'
        assert audiences[1].names.en == 'advanced'
    }

    void "should list 7 rooms"() {
        when:
        List<Room> rooms = extractor.rooms
        then:
        assert rooms.size() == 7
        assert rooms.name.join(', ') == 'Wintergarten, Schauspielhaus, Quantum 1+2, Quantum 3, Quantum 4, Lilaque, Neptun'
    }

    void "should extract all meta data"() {
        when:
        def metaData = MetaData.builder().rooms(extractor.rooms).tracks(extractor.tracks).languages(extractor.languages).defaultLanguage(extractor.defaultLanguage).audiences(extractor.audiences).build()

        then:
        assert metaData
        assert metaData.rooms.size() == 7
        assert metaData.rooms.order.join('') == ('1'..'7').join('')
        assert metaData.rooms.name.join(', ') == 'Wintergarten, Schauspielhaus, Quantum 1+2, Quantum 3, Quantum 4, Lilaque, Neptun'
        assert metaData.tracks.size() == 8
        assert metaData.tracks.names['de'].join(', ') == 'Container & Microservices, Core Java & JVM basierte Sprachen, Enterprise Java & Cloud, Frontend & Mobile, IDEs & Tools, Internet der Dinge, Architektur & Sicherheit, Newcomer'
        assert metaData.defaultLanguage.code == 'de'
        assert metaData.languages.size() == 2
        assert metaData.languages.name.join(', ') == 'Deutsch, English'
        assert metaData.audiences.size() == 2
        assert metaData.audiences.names['de'].join(', ') == 'Anfänger, Fortgeschrittene'
        assert metaData.audiences.names['en'].join(', ') == 'beginners, advanced'
    }

    void "should get conference infos"() {
        when:
        def conference = extractor.conference
        then:
        assert conference.id == 499959
        assert conference.name == 'DukeCon Conference'
        assert conference.url == 'http://dukecon.org'
    }

    void "should get talk types"() {
        when:
        def talkTypes = extractor.talkTypes
        then:
        assert talkTypes.size() == 5
        assert talkTypes.order.join('') == ('1'..'5').join('')
        assert talkTypes.names.de.join(', ') == 'Best Practices, Keynote, Neuerscheinungen oder Features, Projektbericht, Tipps & Tricks'
        assert talkTypes.names.en.join(', ') == 'best practices, keynote, new releases or features , project report, tips & tricks'
    }
}