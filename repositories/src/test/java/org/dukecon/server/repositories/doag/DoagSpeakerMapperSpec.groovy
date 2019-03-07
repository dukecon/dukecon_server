package org.dukecon.server.repositories.doag

import groovy.json.JsonSlurper
import org.dukecon.model.Speaker
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagSpeakerMapperSpec extends Specification {

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
            if (allIds.contains(it)) {
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
        assert mapper.speakers.size() == 112: 'duplicate speakers are removed, 112 left over'

        when:
        def niko = mapper.speakers.find { it.key == '359390' }.value
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
        assert jsonSpeaker.size() == 140: "speaker input contains more speaker (140) than event input (128)"
        mapper.speakers.size() == 117
        println(jsonSpeaker.collect { "${it.VORNAME} ${it.NACHNAME}" })
        println(mapper.speakers.values().name.sort())
        println(jsonSpeaker.collect { "${it.VORNAME} ${it.NACHNAME}" } - mapper.speakers.values().name.sort())
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
        handles.get('Maurice Naftalin') == 'https://twitter.com/mauricenaftalin'
    }

    void "should merge additional twitter handles"() {
        when:
        def jsonEvents = new JsonSlurper().parse(this.class.getResourceAsStream("/javaland-2016.raw"), 'ISO-8859-1').hits.hits._source
        def jsonSpeaker = new JsonSlurper().parse(this.class.getResourceAsStream("/javaland-speaker-2016.raw"), 'ISO-8859-1').hits.hits._source

        and:
        DoagSpeakersMapper mapper = DoagSpeakersMapper.createFrom(jsonEvents, jsonSpeaker, ['Reinier Zwitserloot': 'https://twitter.com/foobar'])

        then:
        mapper.speakers.'371991'.twitter == 'https://twitter.com/foobar'

        when:
        !mapper.speakers.'365616'.twitter

        and:
        mapper.mergeAdditionalTwitterHandles(['Marc Sluiter': 'https://twitter.com/foobar2'])

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

    void "should not override speaker data with empty additional speaker infos"() {
        given:
        def eventInput = new JsonSlurper().parseText('''[{
            "ID_PERSON":"1234",
            "REFERENT_NAME":"Hans Hansen",
            "REFERENT_NACHNAME":"Hansen",
            "REFERENT_FIRMA" : "Firma"}]''')

        def speakerInput = new JsonSlurper().parseText('''[{
            "ID_PERSON":"1234",
            "VORNAME":"Hans",
            "NACHNAME":"Hansen",
            "FIRMA" : "",
            "PROFILFOTO" : "image-data"}]''')

        when:
        DoagSpeakersMapper mapper = DoagSpeakersMapper.createFrom(eventInput, speakerInput)

        then:
        mapper.speakers["1234"].firstname == 'Hans'
        mapper.speakers["1234"].lastname == 'Hansen'
        mapper.speakers["1234"].company == 'Firma'
        mapper.speakers["1234"].photoId == ''
    }
}