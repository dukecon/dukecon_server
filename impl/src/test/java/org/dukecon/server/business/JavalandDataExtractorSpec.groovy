package org.dukecon.server.business

import groovy.json.JsonSlurper
import org.dukecon.model.Audience
import org.dukecon.model.Language
import org.dukecon.model.Room
import org.dukecon.model.Talk
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
        assert conference.metaData.rooms.size() == 7
        assert conference.metaData.rooms.order.join('') == ('1'..'7').join('')
        assert conference.metaData.rooms.name.join(', ') == 'Wintergarten, Schauspielhaus, Quantum 1+2, Quantum 3, Quantum 4, Lilaque, Neptun'
        assert conference.metaData.tracks.size() == 8
        assert conference.metaData.tracks.names['de'].join(', ') == 'Container & Microservices, Core Java & JVM basierte Sprachen, Enterprise Java & Cloud, Frontend & Mobile, IDEs & Tools, Internet der Dinge, Architektur & Sicherheit, Newcomer'
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

    void "should get all speakers"() {
        when:
        def speakers = extractor.speakers.sort {it.id}
        then:
        assert speakers.size() == 116
        assert speakers.first().name == 'Matthias Faix'
        assert speakers.first().company == 'IPM Köln'
        assert speakers.first().id == '146723'
        assert !speakers.first().talks
    }

    void "should get all speakers with their talks"() {
        when:
        def speakers = extractor.speakersWithTalks.sort {it.id}
        then:
        assert speakers.size() == 116
        assert speakers.first().name == 'Matthias Faix'
        assert speakers.first().company == 'IPM Köln'
        assert speakers.first().id == '146723'
        assert speakers.first().talks.size() == 1
        assert speakers.first().talks.first().class == Talk
        assert speakers.find {it.name == 'Roel Spilker'}.talks.size() == 2
        assert speakers.find {it.name == 'Thorben Janssen'}.talks.size() == 2
    }

    void "should get a map of speaker ids to talks held from this speaker"() {
        when:
        def json = new JsonSlurper().parseText('''{ "hits" : { "hits" : [
            {"_source":{"ID_KONGRESS":499959,"ID":509570,"ID_SEMINAR":"509570","FARBCODE":335744,"TRACK":"Internet der Dinge","TRACK_EN":"internet of things","ORDERT":6,"AUDIENCE":"Fortgeschrittene","AUDIENCE_EN":"advanced","DATUM":"2016-03-09T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-09","DATUM_ES":"09.03.2016","BEGINN":"12:00","ENDE":"12:40","TIMESTAMP":"0016-03-07T11:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-07T12:33:28.000+00:53:28","SEMINAR_NR":"51","RAUM_NR":"7","RAUMNAME":"Neptun","AREAID":"7/N","TITEL":"Active Glass","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Matthias Faix","KEYWORDS":",Arduino (Einplatinencomputer),Augmented Reality","REFERENT_NACHNAME":"Faix","REFERENT_FIRMA":"IPM K�ln","ID_PERSON":146723,"ID_PERSON_COREF":null,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Projektbericht","VORTRAGSTYP_EN":"project report","COREFERENT_NAME":null,"COCOREFERENT_NAME":null,"COREFERENT_FIRMA":null,"COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"Wir haben die Idee, Steuerungen von Robotern per Nano Arduino durchzuf�hren. Ein Nano Arduino wurde in ein kleines Geh�use gebaut, welches an eine Brille befestigt wurde. Das Geh�use wurde abgeleitet von einer kleinen Taschenlampe, die an einer Brille befestigt werden kann. In dem Geh�use befindet sich ein Arduino (Nano) und ein Gyroskop und eine kleine Kamera. Wir experimentieren mit folgenden Anwendungen und programmieren darauf den Nano Arduino:\\r\\n\\r\\n- Steuerungen durch Neigung des Kopfes. Man kann XMBC durch Neigung des Kopfes steuern. Also Musikauswahl durch Kopfbewegungen. Fernidee: Schwerbedinderte Menschen k�nnen am Leben teilhaben\\r\\n- Erkennung von GS1 Codes. Bei Erkennung von bestimmten Codes vibriert die Brille. Man k�nnte diese Technik zur  Optimierung von Lagerarbeiten nutzen.\\r\\n\\r\\nBeispiel: Es sollen 10 Dinge in einen Karton gepackt werden. Die Kontrolle erfolgt per Kopplung GS1/Erfassung per Brille. Sobald ein falscher Artikel im Karton gepackt wird, vibriert die Brille.","SPRACHE":"Deutsch","DEMO":"Ja","KEYWORDS_EN":",Arduino (single board computer),Augmented Reality","SPRACHE_EN":"German","DEMO_EN":"yes","BEGINN_EN":null,"ENDE_EN":null}},
            {"_source":{"ID_KONGRESS":499959,"ID":509814,"ID_SEMINAR":"509814","FARBCODE":335743,"TRACK":"IDEs & Tools","TRACK_EN":"IDEs & tools","ORDERT":5,"AUDIENCE":"Anf�nger","AUDIENCE_EN":"beginners","DATUM":"2016-03-08T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-08","DATUM_ES":"08.03.2016","BEGINN":"15:00","ENDE":"15:40","TIMESTAMP":"0016-03-06T14:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-06T15:33:28.000+00:53:28","SEMINAR_NR":"95","RAUM_NR":"6","RAUMNAME":"Lilaque","AREAID":"6/L","TITEL":"Lombok: The Boilerplate Buster. It's a Kind of Magic!","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Roel Spilker","KEYWORDS":",Eclipse (IDE),Gradle,IntelliJ (IDE),Maven,Netbeans (IDE)","REFERENT_NACHNAME":"Spilker","REFERENT_FIRMA":"TOPdesk","ID_PERSON":371581,"ID_PERSON_COREF":371991,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Neuerscheinungen oder Features","VORTRAGSTYP_EN":"new releases or features ","COREFERENT_NAME":"Reinier Zwitserloot","COCOREFERENT_NAME":null,"COREFERENT_FIRMA":"medipc.nl","COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"Lombok is great for getting rid of boilerplate. It plugs directly into your build tools, compilers and IDEs.\\r\\n\\r\\nIngredients:\\r\\n\\r\\n* 1 lombok\\r\\n* Your favourite IDE (Eclipse, Netbeans, IntelliJ IDEA, or Android Studio)\\r\\n* a handful of annotations\\r\\n\\r\\nGently mix with your java code, and __poof__! Your getters, builders, equals implementations, logger variables, and much more magically appear in your outline view and class files, and yet there is no code. No code at all!\\r\\n\\r\\nWith a flourish, the authors of Project Lombok will present the basics, then move on to the exciting new features, such as builders that automatically generate immutable collections, and modifying lombok to fit your personal preferences via the configuration system.\\r\\n\\r\\nThis presentation has something to offer for all java developers, especially those who are looking at other languages.","SPRACHE":"Englisch","DEMO":"Ja","KEYWORDS_EN":",Eclipse (IDE),Gradle,IntelliJ (IDE),Maven,Netbeans (IDE)","SPRACHE_EN":"English","DEMO_EN":"yes","BEGINN_EN":null,"ENDE_EN":null}},
            {"_source":{"ID_KONGRESS":499959,"ID":509720,"ID_SEMINAR":"509720","FARBCODE":335744,"TRACK":"Architektur & Sicherheit","TRACK_EN":"architecture & security","ORDERT":7,"AUDIENCE":"Fortgeschrittene","AUDIENCE_EN":"advanced","DATUM":"2016-03-08T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-08","DATUM_ES":"08.03.2016","BEGINN":"11:00","ENDE":"11:40","TIMESTAMP":"0016-03-06T10:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-06T11:33:28.000+00:53:28","SEMINAR_NR":"83","RAUM_NR":"7","RAUMNAME":"Neptun","AREAID":"7/N","TITEL":"ToTP or Not ToTP, That Is the Question!","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Roel Spilker","KEYWORDS":",Sicherheit,Verschlusselung","REFERENT_NACHNAME":"Spilker","REFERENT_FIRMA":"TOPdesk","ID_PERSON":371581,"ID_PERSON_COREF":371991,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Best Practices","VORTRAGSTYP_EN":"best practices","COREFERENT_NAME":"Reinier Zwitserloot","COCOREFERENT_NAME":null,"COREFERENT_FIRMA":"medipc.nl","COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"Two-factor authentication is more accessible than ever because a smartphone can be found in many a pocket or handbag. Time based one-time passwords (TOTP) give your users the ability to log into your web application not just based on something they know (their password), but with another factor: Something they have. In the case of TOTP, generally a smart phone. Amazon AWS, Google, World of Warcraft, and many others already use this standard to improve authentication security.\\r\\n\\r\\nIt's really easy to add TOTP to your web application, but, like any security measure, it's easy to mess up your implementation. We search the web for a few off the shelf libraries, and delve into the various security problems that these libraries exhibit.\\r\\n\\r\\nThen, when we've listed out all the various security and API concerns, we demo a complete TOTP Web solution in Java, ready to be deployed in your Web application.","SPRACHE":"Englisch","DEMO":"Ja","KEYWORDS_EN":",Security,Encryption","SPRACHE_EN":"English","DEMO_EN":"yes","BEGINN_EN":null,"ENDE_EN":null}},
            {"_source":{"ID_KONGRESS":499959,"ID":509638,"ID_SEMINAR":"509638","FARBCODE":335744,"TRACK":"Enterprise Java & Cloud","TRACK_EN":"enterprise Java & cloud","ORDERT":3,"AUDIENCE":"Fortgeschrittene","AUDIENCE_EN":"advanced","DATUM":"2016-03-08T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-08","DATUM_ES":"08.03.2016","BEGINN":"14:00","ENDE":"14:40","TIMESTAMP":"0016-03-06T13:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-06T14:33:28.000+00:53:28","SEMINAR_NR":"64","RAUM_NR":"2","RAUMNAME":"Schauspielhaus","AREAID":"2/S","TITEL":"CDI 2.0 Deep Dive","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Mark Struberg","KEYWORDS":",Java Enterprise Edition (Java EE)","REFERENT_NACHNAME":"Struberg","REFERENT_FIRMA":"TU Wien","ID_PERSON":368634,"ID_PERSON_COREF":369887,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Tipps & Tricks","VORTRAGSTYP_EN":"tips & tricks","COREFERENT_NAME":"Thorben Janssen","COCOREFERENT_NAME":null,"COREFERENT_FIRMA":null,"COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"In Java EE 8 steht eine umfangreiche Erweiterung der CDI-Spezifikation an. Als aktives \\"Java EE Expert Group\\"-Mitglied und CDI-Container-Entwickler werde ich einen Einblick auf die strukturellen Ideen und Neuerungen in CDI 2.0 geben. \r\n\r\nBeginnend bei den grundlegenden Mechanismen und internen Tricks von CDI-Containern wird ein tiefes Verst�ndnis f�r \\"Contexts and Dependency Injection for Java\\" vermittelt. Ein Hauptaugenmerk wird hierbei auf die neuen Features und Anwendungsbereiche von CDI 2.0 wie asynchrone Mechanismen und die Verwendung von CDI in Java SE gelegt.","SPRACHE":"Deutsch","DEMO":"Nein","KEYWORDS_EN":",Java Enterprise Edition (Java EE)","SPRACHE_EN":"German","DEMO_EN":"no","BEGINN_EN":null,"ENDE_EN":null}},
            {"_source":{"ID_KONGRESS":499959,"ID":509672,"ID_SEMINAR":"509672","FARBCODE":335744,"TRACK":"Enterprise Java & Cloud","TRACK_EN":"enterprise Java & cloud","ORDERT":3,"AUDIENCE":"Fortgeschrittene","AUDIENCE_EN":"advanced","DATUM":"2016-03-08T00:00:00.000+01:00","SIMULTAN":"0","DATUM_ES_EN":"2016-03-08","DATUM_ES":"08.03.2016","BEGINN":"17:00","ENDE":"17:40","TIMESTAMP":"0016-03-06T16:53:28.000+00:53:28","TIMESTAMP_ENDE":"0016-03-06T17:33:28.000+00:53:28","SEMINAR_NR":"72","RAUM_NR":"2","RAUMNAME":"Schauspielhaus","AREAID":"2/S","TITEL":"Effiziente Datenpersistierung mit JPA 2.1 und Hibernate","TITEL_EN":null,"ABSTRACT_EN":null,"REFERENT_NAME":"Thorben Janssen","KEYWORDS":",Datenbank,Java Enterprise Edition (Java EE),Wildfly (Anwendungsserver)","REFERENT_NACHNAME":"Janssen","REFERENT_FIRMA":null,"ID_PERSON":369887,"ID_PERSON_COREF":null,"ID_PERSON_COCOREF":null,"VORTRAGSTYP":"Best Practices","VORTRAGSTYP_EN":"best practices","COREFERENT_NAME":null,"COCOREFERENT_NAME":null,"COREFERENT_FIRMA":null,"COCOREFERENT_FIRMA":null,"ABSTRACT_TEXT":"Daten mit Hilfe der Java Persistence API (JPA) in der Datenbank zu speichern, stellt den Standard f�r Java-Enterprise-Anwendungen dar. Der Applikationsserver stellt alle daf�r ben�tigten Bibliotheken bereits zur Verf�gung, und die Verwendung ist so einfach, dass sie innerhalb k�rzester Zeit erlernt werden kann. Spannend wird es immer erst dann, wenn die Anforderungen steigen, z.B. weil die Datenmenge besonders gro� ist oder die geforderten Antwortzeiten sehr kurz sind. Auch hierf�r bietet JPA in der Regel gute L�sungen, und wenn das nicht ausreicht, k�nnen wir immer noch auf Hibernate-spezifische Features zur�ckgreifen. Dazu sind allerdings deutlich detailliertere Kenntnisse erforderlich. Einige Beispiele daf�r sind die Wahl der richtigen Fetching-Strategie, die Verwendung von Caches und der Einsatz von Bulk-Operationen. \r\n\r\nWir werden uns einen �berblick dar�ber verschaffen, wie wir diese und andere Features nutzen k�nnen, um auch anspruchsvollere Performanceanforderungen umzusetzen. Es werden Kenntnisse in der Verwendung von JPA und Hibernate vorausgesetzt.","SPRACHE":"Deutsch","DEMO":"Ja","KEYWORDS_EN":",Database,Java Enterprise Edition (Java EE),Wildfly (Application Server)","SPRACHE_EN":"German","DEMO_EN":"yes","BEGINN_EN":null,"ENDE_EN":null}}
        ]}}''')
        extractor = new JavalandDataExtractor(talksJson: json.hits.hits._source)
        def speakerIdToTalks = extractor.getSpeakerIdToTalks()
        then:
        assert speakerIdToTalks.size() == 5

        assert speakerIdToTalks['146723'].size() == 1
        assert speakerIdToTalks['146723'].title.join(', ') == 'Active Glass'

        assert speakerIdToTalks['371581'].size() == 2
        assert speakerIdToTalks['371581'].title.join('; ') == 'Lombok: The Boilerplate Buster. It\'s a Kind of Magic!; ToTP or Not ToTP, That Is the Question!'
        assert speakerIdToTalks['371991'].size() == 2
        assert speakerIdToTalks['371991'].title.join('; ') == 'Lombok: The Boilerplate Buster. It\'s a Kind of Magic!; ToTP or Not ToTP, That Is the Question!'

        assert speakerIdToTalks['369887'].size() == 2
        assert speakerIdToTalks['369887'].title.join('; ') == 'Effiziente Datenpersistierung mit JPA 2.1 und Hibernate; CDI 2.0 Deep Dive'
        assert speakerIdToTalks['368634'].size() == 1
        assert speakerIdToTalks['368634'].title.join('; ') == 'CDI 2.0 Deep Dive'
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
        assert !talks.first().speakers.first().defaultSpeaker
        assert talks.first().speakers.first().id == '370942'
        assert talks.first().type.names.de == 'Neuerscheinungen oder Features'
        assert !talks.first().demo
    }
}