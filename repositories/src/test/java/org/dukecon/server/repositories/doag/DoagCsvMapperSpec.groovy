package org.dukecon.server.repositories.doag

import org.dukecon.server.repositories.RawDataResources
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagCsvMapperSpec extends Specification {

    void "should get 49 events from single local csv file"() {
        given:
        DoagCsvMapper mapper = new DoagCsvMapper(new RawDataResources('jfs-2017-formes-dump.csv'))
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
}
