package org.dukecon.server.conference.adpater.doag

import org.dukecon.server.adapter.DefaultRawDataResource
import org.dukecon.server.adapter.doag.DoagJsonMapper
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagJsonMapperSpec extends Specification {
    private DoagJsonMapper mapper

    void setup() {
        mapper = new DoagJsonMapper(new DefaultRawDataResource('javaland-2016.raw'))
    }

    void "should get 110 events from local file"() {
        expect:
        mapper.asMap().events.size() == 110
    }


}
