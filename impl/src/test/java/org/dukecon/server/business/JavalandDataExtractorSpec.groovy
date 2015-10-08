package org.dukecon.server.business

import groovy.json.JsonSlurper
import org.dukecon.model.Audience
import org.dukecon.model.Language
import org.dukecon.model.Room
import spock.lang.Specification


/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class JavalandDataExtractorSpec extends Specification {

    private static JavalandDataExtractor extractor

    void setupSpec() {
        InputStream is = JavalandDataExtractorSpec.class.getResourceAsStream('/javaland-2016.raw')
        JsonSlurper slurper = new JsonSlurper()
        def rawJson = slurper.parse(is, "ISO-8859-1")
        extractor = new JavalandDataExtractor(talksJson: rawJson.hits.hits._source)
    }

    void "should get 8 tracks"(){
        when:
        def tracks = extractor.tracks
        then:
        assert tracks.size() == 8
        assert tracks.order == 1..8
        assert tracks.id == "1".."8"
        assert tracks.names["de"].join(", ") == "Container & Microservices, Core Java & JVM basierte Sprachen, Enterprise Java & Cloud, Frontend & Mobile, IDEs & Tools, Internet der Dinge, Architektur & Sicherheit, Newcomer"
        assert tracks.names["en"].join(", ") == "container & microservices, Core Java & JVM based languages, enterprise Java & cloud, frontend & mobile, IDEs & tools, internet of things, architecture & security, newcomer"
    }

    void "should default language be 'de'"() {
        when:
        Language language = extractor.defaultLanguage
        then:
        assert language.id == 'de'
        assert language.order == 1
        assert language.names.de == 'Deutsch'
    }
    void "should list languages"() {
        when:
        List<Language> languages = extractor.languages
        then:
        assert languages.size() == 2
        assert languages.id.join(', ') == 'de, en'
        assert languages.order.join(', ') == '1, 2'
        assert languages.names.de.join(', ') == 'Deutsch, Englisch'
        assert languages.names.en.join(', ') == 'German, English'
    }

    void "should get iso code from language"() {
        when:
        def en = extractor.getLanguage("English")
        def de = extractor.getLanguage("German")
        then:
        assert en.id == 'en'
        assert de.id == 'de'
    }

    void "should list two audience levels"() {
        when:
        List<Audience> audiences = extractor.audiences
        then:
        assert audiences.size() == 2
        assert audiences[0].id == "1"
        assert audiences[0].order == 1
        assert audiences[0].names.de == 'Anfänger'
        assert audiences[0].names.en == 'beginners'
        assert audiences[1].id == "2"
        assert audiences[1].order == 2
        assert audiences[1].names.de == 'Fortgeschrittene'
        assert audiences[1].names.en == 'advanced'
    }

    void "should list 7 rooms"() {
        when:
        List<Room> rooms = extractor.rooms
        then:
        assert rooms.size() == 7
        assert rooms.id == '1'..'7'
        assert rooms.order == 1..7
        assert rooms.name.join(', ') == 'Wintergarten, Schauspielhaus, Quantum 1+2, Quantum 3, Quantum 4, Lilaque, Neptun'
    }

    void "should extract all meta data"() {
        when:
        def conference = extractor.buildConference()

        then:
        assert conference
        assert conference.rooms.size() == 7
        assert conference.rooms.order.join('') == ('1'..'7').join('')
        assert conference.rooms.name.join(', ') == 'Wintergarten, Schauspielhaus, Quantum 1+2, Quantum 3, Quantum 4, Lilaque, Neptun'
        assert conference.tracks.size() == 8
        assert conference.tracks.names['de'].join(', ') == 'Container & Microservices, Core Java & JVM basierte Sprachen, Enterprise Java & Cloud, Frontend & Mobile, IDEs & Tools, Internet der Dinge, Architektur & Sicherheit, Newcomer'
        assert conference.metaData.defaultLanguage.id == 'de'
        assert conference.metaData.languages.size() == 2
        assert conference.metaData.languages.names.de.join(', ') == 'Deutsch, Englisch'
        assert conference.metaData.languages.names.en.join(', ') == 'German, English'
        assert conference.metaData.audiences.size() == 2
        assert conference.metaData.audiences.names.de.join(', ') == 'Anfänger, Fortgeschrittene'
        assert conference.metaData.audiences.names.en.join(', ') == 'beginners, advanced'
    }

    void "should get conference infos"() {
        when:
        def conference = extractor.buildConference()
        then:
        assert conference.id == '499959'
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

    void "should get talks"() {
        when:
        def talks = extractor.talks.sort {it.id}
        then:
        assert talks.size() == 110
        assert talks.first().title == 'Behavioral Diff als neues Testparadigma'
        assert talks.first().room.name == 'Neptun'
    }
}