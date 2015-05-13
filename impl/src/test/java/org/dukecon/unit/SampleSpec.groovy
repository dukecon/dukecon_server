package org.dukecon.unit

import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class SampleSpec extends Specification {

    def "Test One plus One is Two"() {
        given:
        def a = 1
        when:
        def b = a + a
        then:
        assert b == 2
    }

    def "length of Spock's and his friends' names"() {
        expect:
        name.size() == length

        where:
        name     | length
        "Spock"  | 5
        "Kirk"   | 4
        "Scotty" | 6
    }
}