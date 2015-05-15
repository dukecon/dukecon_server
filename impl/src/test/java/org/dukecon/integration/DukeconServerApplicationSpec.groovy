package org.dukecon.integration

import groovy.transform.TypeChecked
import org.dukecon.DukeConServerApplication
import org.dukecon.model.Talk
import org.dukecon.server.business.TalkProvider
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
@IntegrationTest
@TypeChecked
class DukeconServerApplicationSpec extends Specification {

    @Inject
    TalkProvider talkProvider

    def cleanup() {
        talkProvider.workLocal = false
        talkProvider.talks = []
    }

    void "Should return 2 local talks"() {
        when:
        talkProvider.workLocal = true
        List<Talk> talks = talkProvider.allTalks

        then:
        assert talks.size() == 2
    }

    void "Should return 105 talks"() {
        when:
        List<Talk> talks = talkProvider.allTalks

        then:
        assert talks.size() == 105
    }
}