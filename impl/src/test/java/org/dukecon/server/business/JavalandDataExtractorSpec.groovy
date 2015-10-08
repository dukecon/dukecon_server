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
        extractor = new JavalandDataExtractor(talksJson: readJson().hits.hits._source)
    }

    private readJson() {
        InputStream is = JavalandDataExtractorSpec.class.getResourceAsStream('/javaland-2016.raw')
        JsonSlurper slurper = new JsonSlurper()
        slurper.parse(is, "ISO-8859-1")
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
        assert talkTypes.id.join('') == ('1'..'5').join('')
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

    void "should map one talk field by field"() {
        when:
        def json = new JsonSlurper().parseText('{ "hits" : { "hits" : [ {"_source":{"ID_KONGRESS":499959,"ID":509632,"ID_SEMINAR":"509632","FARBCODE":335744,"TRACK":"Core Java & JVM basierte Sprachen","TRACK_EN":"Core Java & JVM based languages","ORDERT":2,"AUDIENCE":"Fortgeschrittene","AUDIENCE_EN":"advanced","DATUM":"2016-03-09T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-09","DATUM_ES":"09.03.2016","BEGINN":"09:00","ENDE":"09:40","TIMESTAMP":"0016-03-07T08:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-07T09:33:28.000+00:53:28","SEMINAR_NR":"63","RAUM_NR":"1","RAUMNAME":"Wintergarten","AREAID":"1/W","TITEL":"Java\'s Next Big Thing: Value Objects","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Henning Schwentner","KEYWORDS":null,"REFERENT_NACHNAME":"Schwentner","REFERENT_FIRMA":"WPS - Workplace Solutions GmbH","ID_PERSON":370942,"ID_PERSON_COREF":null,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Neuerscheinungen oder Features","VORTRAGSTYP_EN":"new releases or features ","COREFERENT_NAME":null,"COCOREFERENT_NAME":null,"COREFERENT_FIRMA":null,"COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"Nach Lambdas und Co. mit Java 8 ist das \\"Next Big Thing\\" f�r Java die Unterst�tzung von Value Types direkt in der Programmiersprache. Damit bekommt Java ein Feature, das andere Programmiersprachen schon l�nger haben.\\r\\n\\r\\nIn diesem Vortrag schauen wir uns genau an:\\r\\n\\r\\n* was hinter dem Schlachtruf \\"Codes like a class, works like an int\\" steckt\\r\\n* warum value types gleichzeitig effizienteren wie auch besser lesbaren Code erm�glichen\\r\\n* wie der Stand des zugeh�rigen JEP 169 ist\\r\\n* den Unterschied zwischen Reference Types und Value Types\\r\\n* wie Value Types in anderen Sprachen (insbesondere C# und Swift) schon umgesetzt sind\\r\\n* was Vererbung f�r Value Types bedeutet\\r\\n* was die Vorteile von Speicherung auf dem Stack versus Speicherung auf dem Heap sind\\r\\n\\r\\nDer Vortrag wird im \\"Lessig-Style\\" gehalten werden. (https://www.youtube.com/watch?v=RrpajcAgR1E)","SPRACHE":"Deutsch","DEMO":"Nein","KEYWORDS_EN":null,"SPRACHE_EN":"German","DEMO_EN":"no","BEGINN_EN":null,"ENDE_EN":null}}]}}')
        extractor = new JavalandDataExtractor(talksJson: json.hits.hits._source)
        def talks = extractor.talks.sort {it.id}
        then:
        assert talks.size() == 1
        assert talks.first().audience.names.de == 'Fortgeschrittene'
        assert talks.first().language.id == "de"
        assert talks.first().room.id == '1'
        assert talks.first().room.order == 1
        assert talks.first().room.name == "Wintergarten"
        assert talks.first().track.names.de == 'Core Java & JVM basierte Sprachen'
        assert talks.first().track.order == 2
        assert talks.first().track.id == '2'
        assert talks.first().title == 'Java\'s Next Big Thing: Value Objects'
        assert talks.first().id == '509632'
        assert talks.first().start == '2016-03-09T09:00'
        assert talks.first().end == '2016-03-09T09:40'
        assert talks.first().speakers.size() == 1
        assert talks.first().speakers.first().name == 'Henning Schwentner'
        assert talks.first().speakers.first().company == 'WPS - Workplace Solutions GmbH'
        assert talks.first().speakers.first().defaultSpeaker
        assert talks.first().speakers.first().id == '370942'
        assert talks.first().type.names.de == 'Neuerscheinungen oder Features'
        assert !talks.first().demo
    }
}