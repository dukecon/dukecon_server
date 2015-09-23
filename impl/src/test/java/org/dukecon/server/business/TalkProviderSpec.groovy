package org.dukecon.server.business

import org.dukecon.model.Talk
import spock.lang.Specification

import java.time.Instant
import java.time.LocalTime


/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class TalkProviderSpec extends Specification {
    def "test cache is always expired"() {
        when:
        TalkProvider provider = new TalkProvider(cacheLastUpdated: Instant.now(), cacheExpiresAfterSeconds: 0)
        boolean result = provider.isCacheExpired()
        then:
        assert result
        when:
        provider = new TalkProvider(cacheLastUpdated: Instant.EPOCH, cacheExpiresAfterSeconds: 0)
        result = provider.isCacheExpired()
        then:
        assert result
        when:
        provider = new TalkProvider(cacheLastUpdated: Instant.MAX, cacheExpiresAfterSeconds: 0)
        result = provider.isCacheExpired()
        then:
        assert result
    }

    def "test cache is expired"() {
        given:
        TalkProvider provider = new TalkProvider(cacheLastUpdated: Instant.now().minusSeconds(12), cacheExpiresAfterSeconds: 10)
        when:
        boolean result = provider.isCacheExpired()
        then:
        assert result
    }

    def "test cache is not expired when valid until is in future"() {
        given:
        TalkProvider provider = new TalkProvider(cacheLastUpdated: Instant.now().minusSeconds(9), cacheExpiresAfterSeconds: 10)
        when:
        boolean result = provider.isCacheExpired()
        then:
        assert !result
    }


    class MockTalkProvider extends TalkProvider {
        boolean hasReread = false

        void resetHasReread() {
            hasReread = false
        }

        protected void readTalks() {
            hasReread = true
            talks = ['talk1':null]
        }
    }

    void "Should reread talks"() {
        given:
        TalkProvider talkProvider = new MockTalkProvider()
        when:
        def talks = talkProvider.allTalks
        then:
        assert talkProvider.hasReread
        assert talkProvider.talks
        when:
        talkProvider.resetHasReread()
        talks = talkProvider.allTalks
        then:
        assert talkProvider.hasReread
        assert talkProvider.talks
    }

    void "Should not reread talks"() {
        given:
        TalkProvider talkProvider = new MockTalkProvider()
        talkProvider.cacheExpiresAfterSeconds = 10
        when:
        def talks = talkProvider.allTalks
        then:
        assert talkProvider.hasReread
        assert talkProvider.talks
        when:
        talkProvider.resetHasReread()
        talks = talkProvider.allTalks
        then:
        assert !talkProvider.hasReread
        assert talkProvider.talks
    }

}