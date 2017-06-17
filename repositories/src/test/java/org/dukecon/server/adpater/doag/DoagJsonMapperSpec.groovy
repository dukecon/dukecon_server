package org.dukecon.server.conference.adpater.doag

import org.dukecon.server.adapter.RawDataResources
import org.dukecon.server.adapter.doag.DoagJsonMapper
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagJsonMapperSpec extends Specification {

    void "should get 110 events from single local file"() {
        when:
        DoagJsonMapper mapper = new DoagJsonMapper(new RawDataResources('javaland-2016-final-finished-conf.raw'))
        mapper.initMapper()
        then:
        mapper.asMap().size() == 1
        mapper.asMap().eventsData.size() == 142
    }

    void "should get 110 events from multiple local files"() {
        when:
        DoagJsonMapper mapper = new DoagJsonMapper(new RawDataResources([eventsData: 'javaland-2016-final-finished-conf.raw', speakersData: 'javaland-speaker-2016.raw']))
        mapper.initMapper()
        then:
        mapper.asMap().size() == 2
        mapper.asMap().eventsData.size() == 142
        mapper.asMap().speakersData.size() == 140
    }


}
