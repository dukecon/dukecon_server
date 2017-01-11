package org.dukecon.server.conference.adpater.doag

import groovy.json.JsonSlurper
import org.dukecon.server.adapter.doag.DoagSingleSpeakerMapper
import org.dukecon.server.adapter.doag.DoagSpeakersMapper
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagSpeakerDataExtractorSpec extends Specification {

    void "should read single speaker"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":374172,
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