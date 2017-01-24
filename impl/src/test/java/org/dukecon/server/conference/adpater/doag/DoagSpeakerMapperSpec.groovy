package org.dukecon.server.conference.adpater.doag

import groovy.json.JsonSlurper
import org.dukecon.server.adapter.doag.DoagSingleSpeakerMapper
import org.dukecon.server.adapter.doag.DoagSpeakersMapper
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
        def mapper = new DoagSpeakersMapper(json.hits.hits._source)
        then:
        assert mapper.speakers.size() == 112 : 'duplicate speakers are removed, 112 left over'

        when:
        def niko = mapper.speakers.find {it.key == '359390'}.value
        then:
        niko.name == 'Niko Köbler'
        niko.bio.startsWith('Niko macht')
        niko.photoId == '384adc4c17568938801ceab9124c039f'
    }

}