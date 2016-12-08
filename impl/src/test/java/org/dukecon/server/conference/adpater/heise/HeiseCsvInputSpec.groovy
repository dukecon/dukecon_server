package org.dukecon.server.conference.adpater.heise

import org.dukecon.server.adapter.heise.HeiseCsvInput
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HeiseCsvInputSpec extends Specification {
    private HeiseCsvInput input

    void setup() {
        input = new HeiseCsvInput('herbstcampus-2016/herbstcampus_2016_veranstaltungen_20160826.csv')
    }

    void "should 51 lines"() {
        expect:
        input.asMap().events.size() == 51
    }

}
