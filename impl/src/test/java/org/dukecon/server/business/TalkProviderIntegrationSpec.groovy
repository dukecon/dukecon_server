package org.dukecon.server.business

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.DukeConServerApplication
import org.dukecon.model.Talk
import org.dukecon.server.model.Preference
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import javax.inject.Inject


/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = DukeConServerApplication)
@WebAppConfiguration
@IntegrationTest(["server.port=0"])
@Slf4j
@TypeChecked
class TalkProviderIntegrationSpec extends Specification {
    @Inject
    TalkProvider talkProvider

    def cleanup() {
        talkProvider.talks = [:]
    }

    void "Should return 2 local talks"() {
        when:
        talkProvider.talksUri = "resource:/demotalks.json"
        Collection<Talk> talks = talkProvider.allTalks

        then:
        assert talks.size() == 2
    }

    void "Should return 104 talks"() {
        when:
        talkProvider.talksUri = "resource:/javaland-2015.raw"
        Collection<Talk> talks = talkProvider.allTalks

        then:
        assert talks.size() == 104
    }

}