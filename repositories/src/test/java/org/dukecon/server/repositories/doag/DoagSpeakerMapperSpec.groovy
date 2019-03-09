package org.dukecon.server.repositories.doag

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.dukecon.model.Speaker
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Slf4j
class DoagSpeakerMapperSpec extends Specification {

    /**
     * Fragen an die DOAG:
     * doppelte Speaker, werden die aufgeräumt?
     *
     */

    private static final String javalandEvents2016raw = "/javaland-2016.raw"
    private Object loadEventsJson() {
        log.debug("Loading events data from '{}'", javalandEvents2016raw)
        new JsonSlurper().parse(this.class.getResourceAsStream(javalandEvents2016raw), 'ISO-8859-1')
    }

    private static final String javalandSpeaker2016raw = "/javaland-speaker-2016.raw"
    private Object loadSpeakerJson() {
        log.debug("Loading speakers data from '{}'", javalandSpeaker2016raw)
        new JsonSlurper().parse(this.class.getResourceAsStream(javalandSpeaker2016raw), 'ISO-8859-1')
    }

    void "should read testdata"() {
        when:
        def json = loadSpeakerJson()
        then:
        json.hits.hits._source.size() == 140
    }

    @Ignore("We have to define what should happen to the duplicates!")
    void "duplicates are detected"() {
        when:
        def eventsJson = loadEventsJson()
        def speakersJson = loadSpeakerJson()
        def allIds = new HashSet()
        def duplicateSpeakerIds = new TreeSet()
        speakersJson.hits.hits._source.ID_PERSON.each {
            if (allIds.contains(it)) {
                duplicateSpeakerIds.add it
            }
            allIds.add it
        }
        then:
        duplicateSpeakerIds.size() == 28
        duplicateSpeakerIds as List == [270784, 353543, 355126, 364065, 364385, 364697, 365991, 366223, 368414, 368441, 368442, 368512, 368613, 368680, 371413, 371560, 371581, 371592, 371752, 371801, 371857, 371867, 371963, 371987, 371994, 372026, 373653, 373679]

        when:
        def mapper = DoagSpeakersMapper.createFrom(eventsJson.hits.hits._source, speakersJson.hits.hits._source)
        then:
        assert mapper.speakers.size() == 112: "duplicate speakers are removed, 112 left over (${mapper.speakers.size()})"
    }

    void "speaker by key found"() {
        when:
        def eventsJson = loadEventsJson()
        def speakersJson = loadSpeakerJson()
        def mapper = DoagSpeakersMapper.createFrom(eventsJson.hits.hits._source, speakersJson.hits.hits._source)
        def thorben = mapper.speakers.find { it.key == '369887' }.value
        then:
        thorben.name == 'Thorben Janssen'
        thorben.bio.startsWith('Thorben macht')
    }

    void "should extract all speaker and co speaker from event input of javaland 2016"() {
        when:
        def json = loadEventsJson().hits.hits._source

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
        def jsonEvents = loadEventsJson().hits.hits._source
        def jsonSpeaker = loadSpeakerJson().hits.hits._source

        and:
        DoagSpeakersMapper mapper = DoagSpeakersMapper.createFrom(jsonEvents, jsonSpeaker)
        DoagSpeakersMapper mapperEventsOnly = DoagSpeakersMapper.createFrom(jsonEvents, [:])
        DoagSpeakersMapper mapperSpeakersOnly = DoagSpeakersMapper.createFrom([:], jsonSpeaker)

        then:
        log.debug("events: '{}'", mapperEventsOnly.speakers.keySet() - mapperSpeakersOnly.speakers.keySet())
        assert jsonSpeaker.size() == 140: "speaker input contains more speaker (140) than event input (128)"
        mapper.speakers.size() == 117
        log.debug("speakers: '{}'", jsonSpeaker.collect { "${it.VORNAME} ${it.NACHNAME}" })
        log.debug("speakers sorted: '{}'", mapper.speakers.values().name.sort())
        log.debug("remaining speakers: '{}'", jsonSpeaker.collect { "${it.VORNAME} ${it.NACHNAME}" }
                - mapper.speakers.values().name.sort())
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
        def jsonEvents = loadEventsJson().hits.hits._source
        def jsonSpeaker = loadSpeakerJson().hits.hits._source

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
        def jsonEvents = loadEventsJson().hits.hits._source
        def jsonSpeaker = loadSpeakerJson().hits.hits._source

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
        mapper.speakers["1234"].photoId == '1ed1607e23c602e6c0e87a01746b11bf'
    }
}
