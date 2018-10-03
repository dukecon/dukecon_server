package org.dukecon.server.repositories.doag

import org.dukecon.server.repositories.RawDataResources
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagCsvMapperSpec extends Specification {

    void "should get 49 events from single local csv file"() {
        given:
        DoagCsvMapper mapper = new DoagCsvMapper(RawDataResources.of('jfs-2017-formes-dump.csv'))
        when:
        mapper.initMapper()
        def map = mapper.asMap()
        then:
        map.size() == 1
        when:
        def eventsData = map.eventsData
        then:
        eventsData.size() == 49
        when:
        def firstTalk = eventsData.first()
        then:
        firstTalk.ID == '507413'
        firstTalk.REFERENT_NAME == 'Nicolai Mainiero'
        firstTalk.TITEL == '[G4] Property Based Testing'
    }

    void "should get 61 speaker from single local speaker csv file"() {
        given:
        DoagCsvMapper mapper = new DoagCsvMapper(RawDataResources.of(eventsData: 'jfs-2017-formes-dump.csv', speakersData: 'jfs-2017-speaker-formes-dump.csv'))
        when:
        mapper.initMapper()
        def map = mapper.asMap()
        then:
        map.size() == 2
        when:
        def speakersData = map.speakersData
        then:
        speakersData.size() == 63
        when:
        def firstSpeaker = speakersData.first()
        then:
        firstSpeaker.NAME == 'Enno Schulte'
        firstSpeaker.PROFILTEXT.startsWith('Enno Schulte')
        firstSpeaker.ID_PERSON == '373833'
        firstSpeaker.PROFILFOTO == 'EnnoSchulte.jpg'
    }
}
