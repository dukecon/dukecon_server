package org.dukecon.integration

import groovy.transform.TypeChecked
import org.dukecon.DukeConServerApplication
import org.dukecon.model.Talk
import org.dukecon.server.business.TalkProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import javax.inject.Inject

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = DukeConServerApplication)
@WebAppConfiguration
@IntegrationTest
@TypeChecked
class DukeconServerApplicationSpec extends Specification {

    @Inject
    TalkProvider talkProvider

    @Test
    void "Should return 2 local talks"() {
        when:
        talkProvider.workLocal = true
        List<Talk> talks = talkProvider.allTalks
        then:
        assert talks.size() == 2
    }

    @Test
    void "Should return 105 talks"() {
        when:
        List<Talk> talks = talkProvider.allTalks
        then:
        assert talks.size() == 105
    }
}