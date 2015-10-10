package org.dukecon.server.business

import org.dukecon.model.Conference
import org.dukecon.model.Event
import spock.lang.Specification

import java.time.Instant

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class TalkProviderSpec extends Specification {
    def "test cache is always expired"() {
        when:
        JavalandDataProvider provider = new JavalandDataProvider(cacheLastUpdated: Instant.now(), cacheExpiresAfterSeconds: 0)
        boolean result = provider.isCacheExpired()
        then:
        assert result
        when:
        provider = new JavalandDataProvider(cacheLastUpdated: Instant.EPOCH, cacheExpiresAfterSeconds: 0)
        result = provider.isCacheExpired()
        then:
        assert result
        when:
        provider = new JavalandDataProvider(cacheLastUpdated: Instant.MAX, cacheExpiresAfterSeconds: 0)
        result = provider.isCacheExpired()
        then:
        assert result
    }

    def "test cache is expired"() {
        given:
        JavalandDataProvider provider = new JavalandDataProvider(cacheLastUpdated: Instant.now().minusSeconds(12), cacheExpiresAfterSeconds: 10)
        when:
        boolean result = provider.isCacheExpired()
        then:
        assert result
    }

    def "test cache is not expired when valid until is in future"() {
        given:
        JavalandDataProvider provider = new JavalandDataProvider(cacheLastUpdated: Instant.now().minusSeconds(9), cacheExpiresAfterSeconds: 10)
        when:
        boolean result = provider.isCacheExpired()
        then:
        assert !result
    }


    class MockTalkProvider extends JavalandDataProvider {
        boolean hasReread = false

        void resetHasReread() {
            hasReread = false
        }

        @Override
        protected void readData() {
            hasReread = true
            talks = [Event.builder().build()]
            conference = Conference.builder().build()
        }
    }

    void "Should reread events"() {
        given:
        JavalandDataProvider talkProvider = new MockTalkProvider()
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

    void "Should not reread events"() {
        given:
        JavalandDataProvider talkProvider = new MockTalkProvider()
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