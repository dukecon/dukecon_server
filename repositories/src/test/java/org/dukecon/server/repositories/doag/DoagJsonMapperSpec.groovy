package org.dukecon.server.repositories.doag

import org.dukecon.server.repositories.RawDataResources
import org.dukecon.server.repositories.doag.DoagJsonMapper
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagJsonMapperSpec extends Specification {

    void "should get 110 events from single local file"() {
        when:
        DoagJsonMapper mapper = new DoagJsonMapper(RawDataResources.of('javaland-2016.raw'))
        mapper.initMapper()
        then:
        mapper.asMap().size() == 1
        mapper.asMap().eventsData.size() == 110
    }

    void "should get 110 events from multiple local files"() {
        when:
        DoagJsonMapper mapper = new DoagJsonMapper(RawDataResources.of([eventsData: 'javaland-2016.raw', speakersData: 'javaland-speaker-2016.raw']))
        mapper.initMapper()
        then:
        mapper.asMap().size() == 2
        mapper.asMap().eventsData.size() == 142
        mapper.asMap().speakersData.size() == 140
    }


}
