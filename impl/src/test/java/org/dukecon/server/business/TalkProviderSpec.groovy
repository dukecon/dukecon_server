package org.dukecon.server.business

import spock.lang.Specification

import java.time.Instant
import java.time.LocalTime


/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class TalkProviderSpec extends Specification {
    def "test cache is always expired"() {
        given:
        TalkProvider provider = new TalkProvider()
        when:
        boolean result1 = provider.isCacheExpired(Instant.now(), 0)
        boolean result2 = provider.isCacheExpired(Instant.EPOCH, 0)
        boolean result3 = provider.isCacheExpired(Instant.MAX, 0)
        then:
        assert result1 && result2 && result3
    }

    def "test cache is expired"() {
        given:
        TalkProvider provider = new TalkProvider()
        Instant now = Instant.now()
        when:
        boolean result = provider.isCacheExpired(now.minusSeconds(12), 10)
        then:
        assert result
    }

    def "test cache is not expired"() {
        given:
        TalkProvider provider = new TalkProvider()
        Instant now = Instant.now()
        when:
        boolean result = provider.isCacheExpired(now.minusSeconds(9), 10)
        then:
        assert !result
    }

}