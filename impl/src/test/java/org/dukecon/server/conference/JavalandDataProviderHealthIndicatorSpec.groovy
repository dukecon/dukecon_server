package org.dukecon.server.conference

import org.dukecon.DukeConServerApplication
import org.dukecon.model.Conference
import org.dukecon.model.Event
import org.dukecon.model.TalkOld
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.health.Status
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * @author Alexander Schwartz, alexander.schwartz@gmx.net, @ahus1de
 */
class JavalandDataProviderHealthIndicatorSpec extends Specification {

    JavalandDataProviderHealthIndicator healthIndicator;

    void setup() {
        healthIndicator = new JavalandDataProviderHealthIndicator();
        healthIndicator.talkProvider = new JavalandDataProvider();
    }

    void "Should return UP when resource has been read succcessfully"() {
        when:
        healthIndicator.talkProvider.talksUri = "resource:/javaland-2016-testdata.raw"

        then:
        assert healthIndicator.health().status == Status.UP
    }

    void "Should return DOWN when resource doesn't exist"() {
        when:
        healthIndicator.talkProvider.talksUri = "resource:/javaland-notexist.raw"

        then:
        assert healthIndicator.health().status == Status.DOWN
    }

    void "Should return DOWN when resource becomes unavailable"() {
        when:
        // first some data is read
        healthIndicator.talkProvider.talksUri = "resource:/javaland-2016-testdata.raw"
        assert healthIndicator.health().status == Status.UP
        // and then the URL fails
        healthIndicator.talkProvider.talksUri = "resource:/javaland-notexist.raw"
        healthIndicator.talkProvider.cacheExpiresAfterSeconds = 300;
        healthIndicator.talkProvider.cacheLastUpdated = healthIndicator.talkProvider.cacheLastUpdated
                .minusSeconds(healthIndicator.talkProvider.cacheExpiresAfterSeconds + 1);

        then:
        assert healthIndicator.health().status == Status.DOWN
        // ensure that a second call for a cached result will still return DOWN
        assert healthIndicator.health().status == Status.DOWN
    }
}