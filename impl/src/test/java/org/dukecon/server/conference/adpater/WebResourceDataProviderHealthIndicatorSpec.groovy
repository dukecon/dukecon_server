package org.dukecon.server.conference.adpater

import org.dukecon.server.adapter.WebResourceDataProviderHealthIndicator
import org.dukecon.server.javaland.JavalandDataProvider
import org.dukecon.server.javaland.JavalandDataRemote
import org.springframework.boot.actuate.health.Status
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author Alexander Schwartz, alexander.schwartz@gmx.net, @ahus1de
 */
@Ignore
class WebResourceDataProviderHealthIndicatorSpec extends Specification {

    WebResourceDataProviderHealthIndicator healthIndicator;

    void setup() {
        def remote = new JavalandDataRemote(backup: 'javaland-2016-backup.raw')
        def talkProvider = new JavalandDataProvider(remote: remote)
        healthIndicator = new WebResourceDataProviderHealthIndicator(talkProvider, remote);
//        healthIndicator.talkProvider = new JavalandDataProvider();
//        healthIndicator.talkProvider.remote = new JavalandDataRemote();
//        healthIndicator.talkRemote = healthIndicator.talkProvider.remote
//        healthIndicator.talkProvider.remote.backup = "javaland-2016-backup.raw"
    }

    void "Should return UP when resource has been read succcessfully"() {
        when:
        healthIndicator.talkProvider.remote.talksUri = "resource:/javaland-2016-testdata.raw"

        then:
        assert healthIndicator.health().status == Status.UP
    }

    void "Should return DOWN when resource doesn't exist"() {
        when:
        healthIndicator.talkProvider.remote.talksUri = "resource:/javaland-notexist.raw"
        healthIndicator.talkProvider.remote.backup = "javaland-notexist.raw"

        then:
        assert healthIndicator.health().status == Status.DOWN
    }

    void "Should return DOWN when resource becomes unavailable"() {
        when:
        // first some data is read
        healthIndicator.talkProvider.remote.talksUri = "resource:/javaland-2016-testdata.raw"
        assert healthIndicator.health().status == Status.UP
        // and then the URL fails
        healthIndicator.talkProvider.remote.talksUri = "resource:/javaland-notexist.raw"
        healthIndicator.talkProvider.cacheExpiresAfterSeconds = 300;
        healthIndicator.talkProvider.cacheLastUpdated = healthIndicator.talkProvider.cacheLastUpdated
                .minusSeconds(healthIndicator.talkProvider.cacheExpiresAfterSeconds + 1);

        then:
        assert healthIndicator.health().status == Status.DOWN
        // ensure that a second call for a cached result will still return DOWN
        assert healthIndicator.health().status == Status.DOWN
    }

    void "Should return DOWN when backup has been used"() {
        when:
        // first some data is read
        healthIndicator.talkProvider.remote.talksUri = "resource:/javaland-2016-testdata.raw"
        assert healthIndicator.health().status == Status.UP
        // then the backup is called
        healthIndicator.talkProvider.remote.talksUri = "resource:/javaland-notexist.raw"
        healthIndicator.talkProvider.remote.readConferenceDataFallback()

        then:
        assert healthIndicator.health().status == Status.DOWN
    }

}