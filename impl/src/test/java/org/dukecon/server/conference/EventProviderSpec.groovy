package org.dukecon.server.conference

import org.dukecon.model.Conference
import org.dukecon.model.Event
import org.dukecon.server.conference.JavalandDataProvider
import spock.lang.Specification

import java.time.Instant

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class EventProviderSpec extends Specification {
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


    class MockRemote extends JavalandDataRemote {
        boolean hasReread = false

        void resetHasReread() {
            hasReread = false
        }

        @Override
        Conference readConferenceData() {
            hasReread = true
            return Conference.builder().build()
        }
    }

    void "Should reread events"() {
        given:
        JavalandDataProvider talkProvider = new JavalandDataProvider()
        talkProvider.remote = new MockRemote();
        when:
        def conference = talkProvider.conference
        then:
        assert talkProvider.remote.hasReread
        assert talkProvider.conference
        when:
        talkProvider.remote.resetHasReread()
        conference = talkProvider.conference
        then:
        assert talkProvider.remote.hasReread
        assert talkProvider.conference
    }

    void "Should not reread events"() {
        given:
        JavalandDataProvider talkProvider = new JavalandDataProvider()
        talkProvider.remote = new MockRemote();
        talkProvider.cacheExpiresAfterSeconds = 10
        when:
        def conference = talkProvider.conference
        then:
        assert talkProvider.remote.hasReread
        assert talkProvider.conference
        when:
        talkProvider.remote.resetHasReread()
        conference = talkProvider.conference
        then:
        assert !talkProvider.remote.hasReread
        assert talkProvider.conference
    }

}