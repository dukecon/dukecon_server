package org.dukecon.server.repositories.doag

import groovy.json.JsonSlurper
import org.dukecon.model.Speaker
import org.dukecon.server.repositories.doag.DoagDataExtractor
import org.dukecon.server.repositories.doag.DoagSingleSpeakerMapper
import org.dukecon.server.repositories.doag.DoagSpeakersMapper
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagSpeakerMapperSpec extends Specification {

    void "should read single speaker"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":374172,
            "NAME":"Jérôme Boateng",
            "VORNAME":"Jérôme",
            "NACHNAME":"Boateng",
            "WEBSEITE":"http://jeromeboateng.com/?",
            "FIRMA":null,
            "PROFILFOTO":null,
            "PROFILTEXT":"Alle Jahre wieder\r\nkommt das Christuskind\r\nauf die Erde nieder,\r\nwo wir Menschen sind.\r\nKehrt mit seinem Segen\r\nein in jedes Haus,\r\ngeht auf allen Wegen\r\nmit uns ein und aus.\r\nIst auch mir zur Seite\r\nstill und unerkannt,\r\ndaß es treu mich leite\r\nan der lieben Hand.",
            "LINKFACEBOOK":"https://www.facebook.com/JeromeBoateng17/",
            "LINKTWITTER":"https://twitter.com/JB17Official",
            "LINKXING":null,
            "LINKEDIN":null}''')
        then:
        json.ID_PERSON == 374172

        when:
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json)
        then:
        singleSpeakerMapper.speaker.id == "374172"
        singleSpeakerMapper.speaker.name == "Jérôme Boateng"
        singleSpeakerMapper.speaker.website == "http://jeromeboateng.com/?"
        !singleSpeakerMapper.speaker.company
        singleSpeakerMapper.speaker.bio.startsWith('Alle Jahre wieder')
        singleSpeakerMapper.speaker.facebook == "https://www.facebook.com/JeromeBoateng17/"
        singleSpeakerMapper.speaker.twitter == "https://twitter.com/JB17Official"
        !singleSpeakerMapper.speaker.xing
        !singleSpeakerMapper.speaker.linkedin
        !singleSpeakerMapper.speaker.email
        !singleSpeakerMapper.speaker.gplus
    }

    void "should return correct field names by referent type"() {
        expect:
        DoagSingleSpeakerMapper.Type.DEFAULT.idKey == 'ID_PERSON'
        DoagSingleSpeakerMapper.Type.DEFAULT.nameKey == 'NAME'
        DoagSingleSpeakerMapper.Type.DEFAULT.lastnameKey == 'NACHNAME'
        DoagSingleSpeakerMapper.Type.DEFAULT.companyKey == 'FIRMA'

        DoagSingleSpeakerMapper.Type.REFERENT.idKey == 'ID_PERSON'
        DoagSingleSpeakerMapper.Type.REFERENT.nameKey == 'REFERENT_NAME'
        DoagSingleSpeakerMapper.Type.REFERENT.lastnameKey == 'REFERENT_NACHNAME'
        DoagSingleSpeakerMapper.Type.REFERENT.companyKey == 'REFERENT_FIRMA'

        DoagSingleSpeakerMapper.Type.COREFERENT.idKey == 'ID_PERSON_COREF'
        DoagSingleSpeakerMapper.Type.COREFERENT.nameKey == 'COREFERENT_NAME'
        DoagSingleSpeakerMapper.Type.COREFERENT.lastnameKey == 'COREFERENT_NACHNAME'
        DoagSingleSpeakerMapper.Type.COREFERENT.companyKey == 'COREFERENT_FIRMA'

        DoagSingleSpeakerMapper.Type.COCOREFERENT.idKey == 'ID_PERSON_COCOREF'
        DoagSingleSpeakerMapper.Type.COCOREFERENT.nameKey == 'COCOREFERENT_NAME'
        DoagSingleSpeakerMapper.Type.COCOREFERENT.lastnameKey == 'COCOREFERENT_NACHNAME'
        DoagSingleSpeakerMapper.Type.COCOREFERENT.companyKey == 'COCOREFERENT_FIRMA'
    }

    void "should concat first and lastname"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":374172,
            "VORNAME":"Jérôme",
            "NACHNAME":"Boateng"}''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json)
        then:
        singleSpeakerMapper.speaker.firstname == "Jérôme"
        singleSpeakerMapper.speaker.lastname == "Boateng"
        singleSpeakerMapper.speaker.name == "Jérôme Boateng"
    }

    void "should split name in first and last"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":374172,
            "NAME":"Jérôme Boateng"}''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json)
        then:
        singleSpeakerMapper.speaker.firstname == "Jérôme"
        singleSpeakerMapper.speaker.lastname == "Boateng"
        singleSpeakerMapper.speaker.name == "Jérôme Boateng"
    }

    void "should split name in several first and one last"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":374172,
            "NAME":"Jérôme Max Boateng"}''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json)
        then:
        singleSpeakerMapper.speaker.firstname == "Jérôme Max"
        singleSpeakerMapper.speaker.lastname == "Boateng"
        singleSpeakerMapper.speaker.name == "Jérôme Max Boateng"

        when:
        singleSpeakerMapper = new DoagSingleSpeakerMapper(new JsonSlurper().parseText('''{
            "ID_PERSON" : 374172,
            "NAME" : "Hubert Klein Ikkink"}'''))
        then:
        singleSpeakerMapper.speaker.firstname == 'Hubert Klein'
        singleSpeakerMapper.speaker.lastname == 'Ikkink'
        singleSpeakerMapper.speaker.name == 'Hubert Klein Ikkink'

        when:
        singleSpeakerMapper = new DoagSingleSpeakerMapper(new JsonSlurper().parseText('''{
            "ID_PERSON" : 374172,
            "NAME" : "Hubert Klein Ikkink",
            "NACHNAME" : "Klein Ikkink"}'''))
        then:
        singleSpeakerMapper.speaker.firstname == 'Hubert'
        singleSpeakerMapper.speaker.lastname == 'Klein Ikkink'
        singleSpeakerMapper.speaker.name == 'Hubert Klein Ikkink'

        when:
        singleSpeakerMapper = new DoagSingleSpeakerMapper(new JsonSlurper().parseText('''{
            "ID_PERSON" : 374172,
            "NAME" : "Jan Carsten Lohmüller"}'''))
        then:
        singleSpeakerMapper.speaker.firstname == 'Jan Carsten'
        singleSpeakerMapper.speaker.lastname == 'Lohmüller'
        singleSpeakerMapper.speaker.name == 'Jan Carsten Lohmüller'
    }

    void "should not split nor concat first and lastname"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":374172}''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json)
        then:
        !singleSpeakerMapper.speaker.firstname
        !singleSpeakerMapper.speaker.lastname
        !singleSpeakerMapper.speaker.name
    }

    void "should map first and lastname or split name from main referent"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON" : 368455,
            "REFERENT_NAME" : "Andrey Adamovich",
            "REFERENT_NACHNAME" : "Adamovich",
            "REFERENT_FIRMA" : "Aestas/IT",
        }''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json, DoagSingleSpeakerMapper.Type.REFERENT)
        then:
        singleSpeakerMapper.speaker.id == '368455'
        singleSpeakerMapper.speaker.name == 'Andrey Adamovich'
        singleSpeakerMapper.speaker.firstname == 'Andrey'
        singleSpeakerMapper.speaker.lastname == 'Adamovich'
        singleSpeakerMapper.speaker.company == 'Aestas/IT'
    }

    void "should map first and lastname or split name from co referent"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON_COREF" : 371991,
            "COREFERENT_NAME" : "Reinier Zwitserloot",
            "COREFERENT_FIRMA" : "medipc.nl",
        }''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json, DoagSingleSpeakerMapper.Type.COREFERENT)
        then:
        singleSpeakerMapper.speaker.id == '371991'
        singleSpeakerMapper.speaker.name == 'Reinier Zwitserloot'
        singleSpeakerMapper.speaker.firstname == 'Reinier'
        singleSpeakerMapper.speaker.lastname == 'Zwitserloot'
        singleSpeakerMapper.speaker.company == 'medipc.nl'
    }

    void "should map first and lastname or split name from co co referent"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON_COCOREF" : 371991,
            "COCOREFERENT_NAME" : "Reinier Zwitserloot",
            "COCOREFERENT_FIRMA" : "medipc.nl",
        }''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json, DoagSingleSpeakerMapper.Type.COCOREFERENT)
        then:
        singleSpeakerMapper.speaker.id == '371991'
        singleSpeakerMapper.speaker.name == 'Reinier Zwitserloot'
        singleSpeakerMapper.speaker.firstname == 'Reinier'
        singleSpeakerMapper.speaker.lastname == 'Zwitserloot'
        singleSpeakerMapper.speaker.company == 'medipc.nl'
    }

    /**
     * Fragen an die DOAG:
     * doppelte Speaker, werden die aufgeräumt?
     *
     */
    void "should read testdata"() {
        when:
        def json = new JsonSlurper().parse(this.class.getResourceAsStream("/javaland-speaker-2016.raw"), 'ISO-8859-1')
        then:
        json.hits.hits._source.size() == 140

        when:
        def allIds = new HashSet()
        def duplicateSpeakerIds = new TreeSet()
        json.hits.hits._source.ID_PERSON.each {
            if(allIds.contains(it)) {
                duplicateSpeakerIds.add it
            }
            allIds.add it
        }
        then:
        duplicateSpeakerIds.size() == 28
        duplicateSpeakerIds as List == [270784, 353543, 355126, 364065, 364385, 364697, 365991, 366223, 368414, 368441, 368442, 368512, 368613, 368680, 371413, 371560, 371581, 371592, 371752, 371801, 371857, 371867, 371963, 371987, 371994, 372026, 373653, 373679]


        when:
        def mapper = DoagSpeakersMapper.createFrom([:], json.hits.hits._source)
        then:
        assert mapper.speakers.size() == 112 : 'duplicate speakers are removed, 112 left over'

        when:
        def niko = mapper.speakers.find {it.key == '359390'}.value
        then:
        niko.name == 'Niko Köbler'
        niko.bio.startsWith('Niko macht')
        niko.photoId == '384adc4c17568938801ceab9124c039f'
    }

    void "should extract all speaker and co speaker from event input of javaland 2016"() {
        when:
        def json = new JsonSlurper().parse(this.class.getResourceAsStream("/javaland-2016.raw"), 'ISO-8859-1').hits.hits._source

        and:
        def map = [:]
        map.putAll new DoagSpeakersMapper(json, DoagSingleSpeakerMapper.Type.REFERENT).speakers
        map.putAll new DoagSpeakersMapper(json, DoagSingleSpeakerMapper.Type.COREFERENT).speakers
        map.putAll new DoagSpeakersMapper(json, DoagSingleSpeakerMapper.Type.COCOREFERENT).speakers

        then:
        map.size() == 117
    }

    void "should extract all speaker and co speaker from event and speaker input from javaland 2016"() {
        when:
        def jsonEvents = new JsonSlurper().parse(this.class.getResourceAsStream("/javaland-2016.raw"), 'ISO-8859-1').hits.hits._source
        def jsonSpeaker = new JsonSlurper().parse(this.class.getResourceAsStream("/javaland-speaker-2016.raw"), 'ISO-8859-1').hits.hits._source

        and:
        DoagSpeakersMapper mapper = DoagSpeakersMapper.createFrom(jsonEvents, jsonSpeaker)
        DoagSpeakersMapper mapperEventsOnly = DoagSpeakersMapper.createFrom(jsonEvents, [:])
        DoagSpeakersMapper mapperSpeakersOnly = DoagSpeakersMapper.createFrom([:], jsonSpeaker)

        then:
        println(mapperEventsOnly.speakers.keySet() - mapperSpeakersOnly.speakers.keySet())
        assert jsonSpeaker.size() == 140 : "speaker input contains more speaker (140) than event input (128)"
        mapper.speakers.size() == 117
        println (jsonSpeaker.collect {"${it.VORNAME} ${it.NACHNAME}"})
        println (mapper.speakers.values().name.sort())
        println (jsonSpeaker.collect {"${it.VORNAME} ${it.NACHNAME}"} - mapper.speakers.values().name.sort())
        mapper.photos.size() == 7

        mapper.eventIds.size() == 111
        mapper.speakerIds2EventIds.size() == 124

        !mapper.speakers.values().findAll { Speaker s -> !s.firstname || !s.lastname }

        mapper.speakers.'146723'.name == 'Matthias Faix'
        mapper.speakers.'146723'.firstname == 'Matthias'
        mapper.speakers.'146723'.lastname == 'Faix'
        mapper.speakers.'146723'.twitter == null
        mapper.speakers.'146723'.company == 'IPM Köln'
        mapper.speakers.'146723'.website == null
        mapper.speakers.'146723'.bio == null
    }

    void "should read twitter handles from csv"() {
        when:
        def handles = DoagDataExtractor.parseTwitterHandles()

        then:
        handles.size() == 117
        handles.get('Maurice Naftalin') ==  'https://twitter.com/mauricenaftalin'
    }

    void "should merge additional twitter handles"() {
        when:
        def jsonEvents = new JsonSlurper().parse(this.class.getResourceAsStream("/javaland-2016.raw"), 'ISO-8859-1').hits.hits._source
        def jsonSpeaker = new JsonSlurper().parse(this.class.getResourceAsStream("/javaland-speaker-2016.raw"), 'ISO-8859-1').hits.hits._source

        and:
        DoagSpeakersMapper mapper = DoagSpeakersMapper.createFrom(jsonEvents, jsonSpeaker, ['Reinier Zwitserloot':'https://twitter.com/foobar'])

        then:
        mapper.speakers.'371991'.twitter == 'https://twitter.com/foobar'

        when:
        !mapper.speakers.'365616'.twitter

        and:
        mapper.mergeAdditionalTwitterHandles(['Marc Sluiter':'https://twitter.com/foobar2'])

        then:
        mapper.speakers.'365616'.twitter == 'https://twitter.com/foobar2'
    }

    void "should map event ids to speaker"() {
        when:
        def jsonEvents = new JsonSlurper().parse(this.class.getResourceAsStream("/javaland-2016.raw"), 'ISO-8859-1').hits.hits._source
        def jsonSpeaker = new JsonSlurper().parse(this.class.getResourceAsStream("/javaland-speaker-2016.raw"), 'ISO-8859-1').hits.hits._source

        and:
        DoagSpeakersMapper mapper = DoagSpeakersMapper.createFrom(jsonEvents, jsonSpeaker)

        then:
        mapper.eventIds.size() == 111
        mapper.speakerIds2EventIds.size() == 124
        mapper.speakers.size() == 117
        mapper.forEventId("509570").name == 'Matthias Faix'
    }
}