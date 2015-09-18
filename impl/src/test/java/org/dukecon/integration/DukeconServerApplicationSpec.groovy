package org.dukecon.integration

import groovy.transform.TypeChecked

import javax.inject.Inject

import org.dukecon.DukeConServerApplication
import org.dukecon.model.Talk
import org.dukecon.server.business.TalkProvider
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration

import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = DukeConServerApplication)
@WebAppConfiguration
@IntegrationTest(["server.port=0"])
@TypeChecked
class DukeconServerApplicationSpec extends Specification {

    @Inject
    TalkProvider talkProvider

    def cleanup() {
        talkProvider.talks = []
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