package org.dukecon.server.repositories.doag

import groovy.json.JsonSlurper
import org.dukecon.model.Audience
import org.dukecon.model.Conference
import org.dukecon.model.Event
import org.dukecon.model.Language
import org.dukecon.model.Location
import org.dukecon.server.repositories.ConferenceDataExtractor
import org.dukecon.server.repositories.RawDataResources
import org.dukecon.server.conference.ConferencesConfiguration
import org.dukecon.server.javaland.JavalandDataExtractor
import spock.lang.Ignore
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagDataExtractorSpec extends Specification {

    private static ConferenceDataExtractor extractor
    private static Conference conference


    void setupSpec() {
        // TODO Rebuild without SpeakerImageService
        extractor = createExtractor('javaland-2016.raw')
        extractor.rawDataMapper.initMapper()
        conference = extractor.buildConference()
    }

    private DoagDataExtractor createExtractor(String filename) {
        new DoagDataExtractor(ConferencesConfiguration.Conference.of('javaland2016-test', 'DukeCon Conference', 'http://dukecon.org', 'http://javaland.eu'),
                new DoagJsonMapper(RawDataResources.of(filename)), null // new SpeakerImageService()
        )
    }

    private DoagDataExtractor createExtractor(Map files) {
        new DoagDataExtractor(ConferencesConfiguration.Conference.of('javaland2016-test', 'DukeCon Conference', 'http://dukecon.org', 'http://javaland.eu'),
                new DoagJsonMapper(RawDataResources.of(files)), null // new SpeakerImageService()
        )
    }

    void "should get all talks from Javaland 2017"() {
        when:
        def json = new JsonSlurper().parse(DoagDataExtractorSpec.class.getResourceAsStream('/javaland-2017.raw'), "ISO-8859-1")
        then:
        json.hits.hits._source.size() == 144
        when:
//        def conference = DoagDataExtractor.fromFile('javaland-2017.raw', ConferencesConfiguration.Conference.of('javaland2016-test', 'DukeCon Conference', 'http://dukecon.org', 'http://javaland.eu')).buildConference()
        def conference = createExtractor('javaland-2017.raw').buildConference()
        then:
        conference.events.size() == 144
    }

    void "should get 9 tracks"() {
        when:
        def tracks = extractor.tracks
        then:
        assert tracks.size() == 8
        assert tracks.order == (1..8)
        assert tracks.id == ("1".."8")
        assert tracks.names["de"].join(", ") == "Container & Microservices, Core Java & JVM basierte Sprachen, Enterprise Java & Cloud, Frontend & Mobile, IDEs & Tools, Internet der Dinge, Architektur & Sicherheit, Newcomer"
        assert tracks.names["en"].join(", ") == "container & microservices, Core Java & JVM based languages, enterprise Java & cloud, frontend & mobile, IDEs & tools, internet of things, architecture & security, newcomer"
        assert tracks.icon.join(', ') == 'track_1.png, track_2.png, track_3.png, track_4.png, track_5.png, track_6.png, track_7.png, track_8.png'
    }

    void "should default language be 'de'"() {
        when:
        Language language = extractor.defaultLanguage
        then:
        assert language.code == 'de'
        assert language.order == 1
        assert language.names.de == 'Deutsch'
        assert language.icon == 'language_de.png'
    }

    void "should list languages"() {
        when:
        List<Language> languages = extractor.languages
        then:
        assert languages.size() == 2
        assert languages.code.join(', ') == 'de, en'
        assert languages.order.join(', ') == '1, 2'
        assert languages.names.de.join(', ') == 'Deutsch, Englisch'
        assert languages.names.en.join(', ') == 'German, English'
        assert languages.icon.join(', ') == 'language_de.png, language_en.png'
    }

    void "should get iso code from language"() {
        when:
        def en = extractor.getLanguage("English")
        def de = extractor.getLanguage("German")
        then:
        assert en.code == 'en'
        assert de.code == 'de'
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

        assert audiences.icon.join(', ') == 'audience_1.png, audience_2.png'
    }

    void "should list 7 locations"() {
        when:
        List<Location> locations = extractor.locations
        then:
        assert locations.size() == 7
        assert locations.id.join(', ') == '1, 2, 3, 4, 5, 6, 7'
        assert locations.order.join(', ') == '1, 2, 3, 4, 5, 6, 7'
        assert locations.names.de.join(', ') == 'Wintergarten, Schauspielhaus, Quantum 1+2, Quantum 3, Quantum 4, Lilaque, Neptun'
        assert locations.icon.join(', ') == 'location_1.png, location_2.png, location_3.png, location_4.png, location_5.png, location_6.png, location_7.png'
    }

    void "should list room capacities"() {
        when:
//        def doagDataExtractor = DoagDataExtractor.fromFile('javaland-2017.raw', ConferencesConfiguration.Conference.of('javaland2016-test', 'DukeCon Conference', 'http://dukecon.org', 'http://javaland.eu'))
        def doagDataExtractor = createExtractor('javaland-2017.raw')
        and:
        doagDataExtractor.buildConference()
        then:
        doagDataExtractor.locations.names.de.join(', ') == 'Silverado Theater, JUG-Café, Workshop-Raum Juno, JavaInnovationLab, Quantum 1, Quantum 2, Quantum 3, Quantum 4, Dambali (Hotel Matamba), Bambuti (Hotel Matamba), Wang Wei (Hotel Ling Bao), Wintergarten, Konfuzius (Hotel Ling Bao), Schauspielhaus, Quantum 1+2, Quantum 3+4, Eventhalle, Neptun, Quantum UG , Community Hall'
        doagDataExtractor.locations.capacity.join(', ') == '1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 850, 0, 460, 250, 250, 400, 120, 60, 0'
        doagDataExtractor.locations.collectEntries {
            [(it.names.de): it.capacity]
        }.inject([]) { result, entry -> result << "${entry.key}: ${entry.value}" }.join(', ') == 'Silverado Theater: 1000, JUG-Café: 0, Workshop-Raum Juno: 0, JavaInnovationLab: 0, Quantum 1: 0, Quantum 2: 0, Quantum 3: 0, Quantum 4: 0, Dambali (Hotel Matamba): 0, Bambuti (Hotel Matamba): 0, Wang Wei (Hotel Ling Bao): 0, Wintergarten: 850, Konfuzius (Hotel Ling Bao): 0, Schauspielhaus: 460, Quantum 1+2: 250, Quantum 3+4: 250, Eventhalle: 400, Neptun: 120, Quantum UG : 60, Community Hall: 0'
    }

    void "should extract all meta data"() {
        when:
        def conference = extractor.buildConference()

        then:
        assert conference
        assert conference.metaData.locations.size() == 7
        assert conference.metaData.locations.order.join(', ') == '1, 2, 3, 4, 5, 6, 7'
        assert conference.metaData.locations.names.de.join(', ') == 'Wintergarten, Schauspielhaus, Quantum 1+2, Quantum 3, Quantum 4, Lilaque, Neptun'
        assert conference.metaData.tracks.size() == 8
        assert conference.metaData.tracks.names['de'].join(', ') == 'Container & Microservices, Core Java & JVM basierte Sprachen, Enterprise Java & Cloud, Frontend & Mobile, IDEs & Tools, Internet der Dinge, Architektur & Sicherheit, Newcomer'
        assert conference.metaData.defaultLanguage.code == 'de'
        assert conference.metaData.defaultIcon == 'Unknown.png'
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
        assert conference.id == 'javaland2016-test'
        assert conference.name == 'DukeCon Conference'
        assert conference.url == 'http://dukecon.org'
        assert conference.homeUrl == 'http://javaland.eu'
    }

    void "should get event types"() {
        when:
        def eventTypes = extractor.eventTypes
        then:
        assert eventTypes.size() == 5
        assert eventTypes.id.join('') == ('1'..'5').join('')
        assert eventTypes.order.join('') == ('1'..'5').join('')
        assert eventTypes.names.de.join(', ') == 'Best Practices, Keynote, Neuerscheinungen oder Features, Projektbericht, Tipps & Tricks'
        assert eventTypes.names.en.join(', ') == 'best practices, keynote, new releases or features , project report, tips & tricks'
        assert eventTypes.icon.join(', ') == 'eventType_1.png, eventType_2.png, eventType_3.png, eventType_4.png, eventType_5.png'
    }

    void "should get events"() {
        when:
        def events = extractor.events.sort { it.id }
        then:
        assert events.size() == 111
        assert events.first().title == 'Behavioral Diff als neues Testparadigma'
        assert events.first().location.names.de == 'Neptun'
    }

    void "should get all speakers"() {
        when:
        def speakers = extractor.speakers.sort { it.id }
        then:
        assert speakers.size() >= 116
        when:
        def speaker = speakers.find { it.name == 'Matthias Faix' }
        then:
        assert speaker.name == 'Matthias Faix'
        assert speaker.company == 'IPM Köln'
        assert speaker.id == '146723'
        assert !speaker.events
    }

    void "should get all speakers with their events"() {
        when:
        def speakers = conference.speakers.sort { it.id }
        then:
        assert speakers.size() >= 116
        when:
        def speaker = speakers.find { it.name == 'Matthias Faix' }
        then:
        assert speaker.name == 'Matthias Faix'
        assert speaker.company == 'IPM Köln'
        assert speaker.id == '146723'
        assert speaker.events.size() == 1
        assert speaker.events.first().class == Event

        assert speakers.find { it.name == 'Roel Spilker' }.events.size() == 2
        assert speakers.find { it.name == 'Thorben Janssen' }.events.size() == 2
    }

    @Ignore
    void "should get a map of speaker ids to events held from this speaker"() {
        when:
        def json = new JsonSlurper().parseText('''{ "hits" : { "hits" : [
            {"_source":{"ID_KONGRESS":499959,"ID":509570,"ID_SEMINAR":"509570","FARBCODE":335744,"TRACK":"Internet der Dinge","TRACK_EN":"internet of things","ORDERT":6,"AUDIENCE":"Fortgeschrittene","AUDIENCE_EN":"advanced","DATUM":"2016-03-09T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-09","DATUM_ES":"09.03.2016","BEGINN":"12:00","ENDE":"12:40","TIMESTAMP":"0016-03-07T11:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-07T12:33:28.000+00:53:28","SEMINAR_NR":"51","RAUM_NR":"7","RAUMNAME":"Neptun","AREAID":"7/N","TITEL":"Active Glass","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Matthias Faix","KEYWORDS":",Arduino (Einplatinencomputer),Augmented Reality","REFERENT_NACHNAME":"Faix","REFERENT_FIRMA":"IPM K�ln","ID_PERSON":146723,"ID_PERSON_COREF":null,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Projektbericht","VORTRAGSTYP_EN":"project report","COREFERENT_NAME":null,"COCOREFERENT_NAME":null,"COREFERENT_FIRMA":null,"COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"Wir haben die Idee, Steuerungen von Robotern per Nano Arduino durchzuf�hren. Ein Nano Arduino wurde in ein kleines Geh�use gebaut, welches an eine Brille befestigt wurde. Das Geh�use wurde abgeleitet von einer kleinen Taschenlampe, die an einer Brille befestigt werden kann. In dem Geh�use befindet sich ein Arduino (Nano) und ein Gyroskop und eine kleine Kamera. Wir experimentieren mit folgenden Anwendungen und programmieren darauf den Nano Arduino:\\r\\n\\r\\n- Steuerungen durch Neigung des Kopfes. Man kann XMBC durch Neigung des Kopfes steuern. Also Musikauswahl durch Kopfbewegungen. Fernidee: Schwerbedinderte Menschen k�nnen am Leben teilhaben\\r\\n- Erkennung von GS1 Codes. Bei Erkennung von bestimmten Codes vibriert die Brille. Man k�nnte diese Technik zur  Optimierung von Lagerarbeiten nutzen.\\r\\n\\r\\nBeispiel: Es sollen 10 Dinge in einen Karton gepackt werden. Die Kontrolle erfolgt per Kopplung GS1/Erfassung per Brille. Sobald ein falscher Artikel im Karton gepackt wird, vibriert die Brille.","SPRACHE":"Deutsch","DEMO":"Ja","KEYWORDS_EN":",Arduino (single board computer),Augmented Reality","SPRACHE_EN":"German","DEMO_EN":"yes","BEGINN_EN":null,"ENDE_EN":null}},
            {"_source":{"ID_KONGRESS":499959,"ID":509814,"ID_SEMINAR":"509814","FARBCODE":335743,"TRACK":"IDEs & Tools","TRACK_EN":"IDEs & tools","ORDERT":5,"AUDIENCE":"Anf�nger","AUDIENCE_EN":"beginners","DATUM":"2016-03-08T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-08","DATUM_ES":"08.03.2016","BEGINN":"15:00","ENDE":"15:40","TIMESTAMP":"0016-03-06T14:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-06T15:33:28.000+00:53:28","SEMINAR_NR":"95","RAUM_NR":"6","RAUMNAME":"Lilaque","AREAID":"6/L","TITEL":"Lombok: The Boilerplate Buster. It's a Kind of Magic!","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Roel Spilker","KEYWORDS":",Eclipse (IDE),Gradle,IntelliJ (IDE),Maven,Netbeans (IDE)","REFERENT_NACHNAME":"Spilker","REFERENT_FIRMA":"TOPdesk","ID_PERSON":371581,"ID_PERSON_COREF":371991,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Neuerscheinungen oder Features","VORTRAGSTYP_EN":"new releases or features ","COREFERENT_NAME":"Reinier Zwitserloot","COCOREFERENT_NAME":null,"COREFERENT_FIRMA":"medipc.nl","COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"Lombok is great for getting rid of boilerplate. It plugs directly into your build tools, compilers and IDEs.\\r\\n\\r\\nIngredients:\\r\\n\\r\\n* 1 lombok\\r\\n* Your favourite IDE (Eclipse, Netbeans, IntelliJ IDEA, or Android Studio)\\r\\n* a handful of annotations\\r\\n\\r\\nGently mix with your java code, and __poof__! Your getters, builders, equals implementations, logger variables, and much more magically appear in your outline view and class files, and yet there is no code. No code at all!\\r\\n\\r\\nWith a flourish, the authors of Project Lombok will present the basics, then move on to the exciting new features, such as builders that automatically generate immutable collections, and modifying lombok to fit your personal preferences via the configuration system.\\r\\n\\r\\nThis presentation has something to offer for all java developers, especially those who are looking at other languages.","SPRACHE":"Englisch","DEMO":"Ja","KEYWORDS_EN":",Eclipse (IDE),Gradle,IntelliJ (IDE),Maven,Netbeans (IDE)","SPRACHE_EN":"English","DEMO_EN":"yes","BEGINN_EN":null,"ENDE_EN":null}},
            {"_source":{"ID_KONGRESS":499959,"ID":509720,"ID_SEMINAR":"509720","FARBCODE":335744,"TRACK":"Architektur & Sicherheit","TRACK_EN":"architecture & security","ORDERT":7,"AUDIENCE":"Fortgeschrittene","AUDIENCE_EN":"advanced","DATUM":"2016-03-08T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-08","DATUM_ES":"08.03.2016","BEGINN":"11:00","ENDE":"11:40","TIMESTAMP":"0016-03-06T10:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-06T11:33:28.000+00:53:28","SEMINAR_NR":"83","RAUM_NR":"7","RAUMNAME":"Neptun","AREAID":"7/N","TITEL":"ToTP or Not ToTP, That Is the Question!","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Roel Spilker","KEYWORDS":",Sicherheit,Verschlusselung","REFERENT_NACHNAME":"Spilker","REFERENT_FIRMA":"TOPdesk","ID_PERSON":371581,"ID_PERSON_COREF":371991,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Best Practices","VORTRAGSTYP_EN":"best practices","COREFERENT_NAME":"Reinier Zwitserloot","COCOREFERENT_NAME":null,"COREFERENT_FIRMA":"medipc.nl","COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"Two-factor authentication is more accessible than ever because a smartphone can be found in many a pocket or handbag. Time based one-time passwords (TOTP) give your users the ability to log into your web application not just based on something they know (their password), but with another factor: Something they have. In the case of TOTP, generally a smart phone. Amazon AWS, Google, World of Warcraft, and many others already use this standard to improve authentication security.\\r\\n\\r\\nIt's really easy to add TOTP to your web application, but, like any security measure, it's easy to mess up your implementation. We search the web for a few off the shelf libraries, and delve into the various security problems that these libraries exhibit.\\r\\n\\r\\nThen, when we've listed out all the various security and API concerns, we demo a complete TOTP Web solution in Java, ready to be deployed in your Web application.","SPRACHE":"Englisch","DEMO":"Ja","KEYWORDS_EN":",Security,Encryption","SPRACHE_EN":"English","DEMO_EN":"yes","BEGINN_EN":null,"ENDE_EN":null}},
            {"_source":{"ID_KONGRESS":499959,"ID":509638,"ID_SEMINAR":"509638","FARBCODE":335744,"TRACK":"Enterprise Java & Cloud","TRACK_EN":"enterprise Java & cloud","ORDERT":3,"AUDIENCE":"Fortgeschrittene","AUDIENCE_EN":"advanced","DATUM":"2016-03-08T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-08","DATUM_ES":"08.03.2016","BEGINN":"14:00","ENDE":"14:40","TIMESTAMP":"0016-03-06T13:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-06T14:33:28.000+00:53:28","SEMINAR_NR":"64","RAUM_NR":"2","RAUMNAME":"Schauspielhaus","AREAID":"2/S","TITEL":"CDI 2.0 Deep Dive","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Mark Struberg","KEYWORDS":",Java Enterprise Edition (Java EE)","REFERENT_NACHNAME":"Struberg","REFERENT_FIRMA":"TU Wien","ID_PERSON":368634,"ID_PERSON_COREF":369887,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Tipps & Tricks","VORTRAGSTYP_EN":"tips & tricks","COREFERENT_NAME":"Thorben Janssen","COCOREFERENT_NAME":null,"COREFERENT_FIRMA":null,"COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"In Java EE 8 steht eine umfangreiche Erweiterung der CDI-Spezifikation an. Als aktives \\"Java EE Expert Group\\"-Mitglied und CDI-Container-Entwickler werde ich einen Einblick auf die strukturellen Ideen und Neuerungen in CDI 2.0 geben. \r\n\r\nBeginnend bei den grundlegenden Mechanismen und internen Tricks von CDI-Containern wird ein tiefes Verst�ndnis f�r \\"Contexts and Dependency Injection for Java\\" vermittelt. Ein Hauptaugenmerk wird hierbei auf die neuen Features und Anwendungsbereiche von CDI 2.0 wie asynchrone Mechanismen und die Verwendung von CDI in Java SE gelegt.","SPRACHE":"Deutsch","DEMO":"Nein","KEYWORDS_EN":",Java Enterprise Edition (Java EE)","SPRACHE_EN":"German","DEMO_EN":"no","BEGINN_EN":null,"ENDE_EN":null}},
            {"_source":{"ID_KONGRESS":499959,"ID":509672,"ID_SEMINAR":"509672","FARBCODE":335744,"TRACK":"Enterprise Java & Cloud","TRACK_EN":"enterprise Java & cloud","ORDERT":3,"AUDIENCE":"Fortgeschrittene","AUDIENCE_EN":"advanced","DATUM":"2016-03-08T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-08","DATUM_ES":"08.03.2016","BEGINN":"17:00","ENDE":"17:40","TIMESTAMP":"0016-03-06T16:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-06T17:33:28.000+00:53:28","SEMINAR_NR":"72","RAUM_NR":"2","RAUMNAME":"Schauspielhaus","AREAID":"2/S","TITEL":"Effiziente Datenpersistierung mit JPA 2.1 und Hibernate","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Thorben Janssen","KEYWORDS":",Datenbank,Java Enterprise Edition (Java EE),Wildfly (Anwendungsserver)","REFERENT_NACHNAME":"Janssen","REFERENT_FIRMA":null,"ID_PERSON":369887,"ID_PERSON_COREF":null,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Best Practices","VORTRAGSTYP_EN":"best practices","COREFERENT_NAME":null,"COCOREFERENT_NAME":null,"COREFERENT_FIRMA":null,"COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"Daten mit Hilfe der Java Persistence API (JPA) in der Datenbank zu speichern, stellt den Standard f�r Java-Enterprise-Anwendungen dar. Der Applikationsserver stellt alle daf�r ben�tigten Bibliotheken bereits zur Verf�gung, und die Verwendung ist so einfach, dass sie innerhalb k�rzester Zeit erlernt werden kann. Spannend wird es immer erst dann, wenn die Anforderungen steigen, z.B. weil die Datenmenge besonders gro� ist oder die geforderten Antwortzeiten sehr kurz sind. Auch hierf�r bietet JPA in der Regel gute L�sungen, und wenn das nicht ausreicht, k�nnen wir immer noch auf Hibernate-spezifische Features zur�ckgreifen. Dazu sind allerdings deutlich detailliertere Kenntnisse erforderlich. Einige Beispiele daf�r sind die Wahl der richtigen Fetching-Strategie, die Verwendung von Caches und der Einsatz von Bulk-Operationen. \r\n\r\nWir werden uns einen �berblick dar�ber verschaffen, wie wir diese und andere Features nutzen k�nnen, um auch anspruchsvollere Performanceanforderungen umzusetzen. Es werden Kenntnisse in der Verwendung von JPA und Hibernate vorausgesetzt.","SPRACHE":"Deutsch","DEMO":"Ja","KEYWORDS_EN":",Database,Java Enterprise Edition (Java EE),Wildfly (Application Server)","SPRACHE_EN":"German","DEMO_EN":"yes","BEGINN_EN":null,"ENDE_EN":null}}
        ]}}''')
        // TODO: rewrite to use DoagDataExtractor
        extractor = new JavalandDataExtractor(talksJson: json.hits.hits._source)
        def speakerIdToEvents = extractor.getSpeakerIdToEvents()
        then:
        assert speakerIdToEvents.size() == 5

        assert speakerIdToEvents['146723'].size() == 1
        assert speakerIdToEvents['146723'].title.join(', ') == 'Active Glass'

        assert speakerIdToEvents['371581'].size() == 2
        assert speakerIdToEvents['371581'].title.join('; ') == 'Lombok: The Boilerplate Buster. It\'s a Kind of Magic!; ToTP or Not ToTP, That Is the Question!'
        assert speakerIdToEvents['371991'].size() == 2
        assert speakerIdToEvents['371991'].title.join('; ') == 'Lombok: The Boilerplate Buster. It\'s a Kind of Magic!; ToTP or Not ToTP, That Is the Question!'

        assert speakerIdToEvents['369887'].size() == 2
        assert speakerIdToEvents['369887'].title.join('; ') == 'Effiziente Datenpersistierung mit JPA 2.1 und Hibernate; CDI 2.0 Deep Dive'
        assert speakerIdToEvents['368634'].size() == 1
        assert speakerIdToEvents['368634'].title.join('; ') == 'CDI 2.0 Deep Dive'
    }

    void "should map one talk field by field"() {
        when:
        def json = new JsonSlurper().parseText('{ "hits" : { "hits" : [ {"_source":{"ID_KONGRESS":499959,"ID":509632,"ID_SEMINAR":"509632","FARBCODE":335744,"TRACK":"Core Java & JVM basierte Sprachen","TRACK_EN":"Core Java & JVM based languages","ORDERT":2,"AUDIENCE":"Fortgeschrittene","AUDIENCE_EN":"advanced","DATUM":"2016-03-09T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-09","DATUM_ES":"09.03.2016","BEGINN":"09:00","ENDE":"09:40","TIMESTAMP":"0016-03-07T08:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-07T09:33:28.000+00:53:28","SEMINAR_NR":"63","RAUM_NR":"1","RAUMNAME":"Wintergarten","AREAID":"1/W","TITEL":"Java\'s Next Big Thing: Value Objects","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Henning Schwentner","KEYWORDS":null,"REFERENT_NACHNAME":"Schwentner","REFERENT_FIRMA":"WPS - Workplace Solutions GmbH","ID_PERSON":370942,"ID_PERSON_COREF":null,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Neuerscheinungen oder Features","VORTRAGSTYP_EN":"new releases or features ","COREFERENT_NAME":null,"COCOREFERENT_NAME":null,"COREFERENT_FIRMA":null,"COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"Nach Lambdas und Co. mit Java 8 ist das \\"Next Big Thing\\" f�r Java die Unterst�tzung von Value Types direkt in der Programmiersprache. Damit bekommt Java ein Feature, das andere Programmiersprachen schon l�nger haben.\\r\\n\\r\\nIn diesem Vortrag schauen wir uns genau an:\\r\\n\\r\\n* was hinter dem Schlachtruf \\"Codes like a class, works like an int\\" steckt\\r\\n* warum value types gleichzeitig effizienteren wie auch besser lesbaren Code erm�glichen\\r\\n* wie der Stand des zugeh�rigen JEP 169 ist\\r\\n* den Unterschied zwischen Reference Types und Value Types\\r\\n* wie Value Types in anderen Sprachen (insbesondere C# und Swift) schon umgesetzt sind\\r\\n* was Vererbung f�r Value Types bedeutet\\r\\n* was die Vorteile von Speicherung auf dem Stack versus Speicherung auf dem Heap sind\\r\\n\\r\\nDer Vortrag wird im \\"Lessig-Style\\" gehalten werden. (https://www.youtube.com/watch?v=RrpajcAgR1E)","SPRACHE":"Deutsch","DEMO":"Nein","KEYWORDS_EN":null,"SPRACHE_EN":"German","DEMO_EN":"no","BEGINN_EN":null,"ENDE_EN":null, "AUSGEBUCHT":1}}]}}')
        // TODO: use DoagDataExtractor
        def extractor = new JavalandDataExtractor(talksJson: json.hits.hits._source)
        def events = extractor.events.sort { it.id }
        then:
        assert events.size() == 1
        assert events.first().audience.names.de == 'Fortgeschrittene'
        assert events.first().language.code == "de"
        assert events.first().location.id == '1'
        assert events.first().location.order == 1
        assert events.first().location.names.de == "Wintergarten"
        assert events.first().track.names.de == 'Core Java & JVM basierte Sprachen'
        assert events.first().track.order == 2
        assert events.first().track.id == '1'
        assert events.first().title == 'Java\'s Next Big Thing: Value Objects'
        assert events.first().id == '509632'
        assert events.first().start == LocalDateTime.parse('2016-03-09 09:00:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        assert events.first().end == LocalDateTime.parse('2016-03-09 09:40:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        assert events.first().speakers.size() == 1
        assert events.first().speakers.first().name == 'Henning Schwentner'
        assert events.first().speakers.first().company == 'WPS - Workplace Solutions GmbH'
        assert events.first().speakers.first().id == '370942'
        assert events.first().type.names.de == 'Neuerscheinungen oder Features'
        assert !events.first().demo
//        assert !events.first().fullyBooked
//        assert events.first().veryPopular
//        assert events.first().numberOfFavorites == 0
    }

    void "should read time stamps from Java Forum Stuttgart"() {
        given:
        // TODO Rebuild without SpeakerImageService
//        def extractor = new DoagDataExtractor(ConferencesConfiguration.Conference.of('jfs2016-test', 'DukeCon Conference', 'http://dukecon.org', 'http://javaland.eu'),
//                new DoagJsonMapper(RawDataResources.of('jfs-2016-final-finished-conf.raw.json')), null // new SpeakerImageService()
//        )
        def extractor = createExtractor('jfs-2016-final-finished-conf.raw.json')
        when:
        extractor.rawDataMapper.initMapper()
        extractor.buildConference()
        def events = extractor.events
        then:
        '2016-07-07T15:35' == events.first().start.toString()
        '2016-07-07T16:20' == events.first().end.toString()
        events.each {
            assert it.start
            assert it.end
        }
    }

    void "read keywords and additional documents"() {
        given:
        def extractor = createExtractor([eventsData: 'javaland-2017.raw', additionalData: 'javaland-additional-2016.raw'])
        when:
        extractor.rawDataMapper.initMapper()
        def conference = extractor.buildConference()
        then:
        conference.events.size() == 144
        conference.events.findAll { e -> e.keywords.en }.size() == 81
        conference.events.findAll { e -> e.keywords.en }.first().keywords.en == ['Cloud Computing', 'GlassFish (Application Server)', 'Java Enterprise Edition (Java EE)', 'Middleware', 'WebLogic (Application Server)', 'Wildfly (Application Server)']
        conference.events.findAll { e -> e.documents.slides }.size() == 74
        conference.events.findAll { e -> e.documents.manuscript }.size() == 0
        conference.events.findAll { e -> e.documents.other }.size() == 7
    }

    void "encoding with Windows-1252 special apostroph"() {
        given:
        def extractor = createExtractor('javaland-2019-encoding-test.json')
        when:
        extractor.rawDataMapper.initMapper()
        def conference = extractor.buildConference()
        then:
        conference.events.size() == 1
        conference.events.first().speakers.first().name == 'Anja Papenfuß-Straub'
        conference.events.first().speakers.first().company == 'ING DiBa AG'
        conference.events.first().id == '569936'
        conference.events.first().title == 'Edit’n’P(r)ay? Oder vielleicht doch besser testen?'
    }
}