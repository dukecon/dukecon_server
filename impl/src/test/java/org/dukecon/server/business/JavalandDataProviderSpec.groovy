package org.dukecon.server.business

import groovy.util.logging.Slf4j
import org.dukecon.DukeConServerApplication
import org.dukecon.model.Talk
import org.junit.After
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
//@TypeChecked
class JavalandDataProviderSpec extends Specification {
    @Inject
    JavalandDataProvider dataProvider

    def cleanup() {
        dataProvider.clearCache()
    }

    void "Should return 104 talks (2015)"() {
        when:
        dataProvider.talksUri = "resource:/javaland-2015.raw"
        Collection<Talk> talks = dataProvider.allTalks

        then:
        assert talks.size() == 104
        assert dataProvider.metaData
        assert dataProvider.metaData.rooms.size() == 7
        assert dataProvider.metaData.tracks.size() == 10
        assert dataProvider.metaData.defaultLanguage.code == 'de'
        assert dataProvider.metaData.languages.size() == 2
        assert dataProvider.metaData.audiences.size() == 2
    }

    void "Should return 110 talks (2016)"() {
        when:
        dataProvider.talksUri = "resource:/javaland-2016.raw"
        Collection<Talk> talks = dataProvider.allTalks

        then:
        assert talks.size() == 110
        assert dataProvider.metaData
        assert dataProvider.metaData.rooms.size() == 7
        assert dataProvider.metaData.rooms.order.join('') == ('1'..'7').join('')
        assert dataProvider.metaData.tracks.size() == 8
        assert dataProvider.metaData.defaultLanguage.code == 'de'
        assert dataProvider.metaData.languages.size() == 2
        assert dataProvider.metaData.audiences.size() == 2
    }
}