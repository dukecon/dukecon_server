package org.dukecon.server.business

import groovy.util.logging.Slf4j
import org.dukecon.DukeConServerApplication
import org.dukecon.model.Event
import org.dukecon.server.conference.JavalandDataProvider
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

    void "Should return 110 events (2016)"() {
        when:
        dataProvider.talksUri = "resource:/javaland-2016.raw"
        Collection<Event> events = dataProvider.allTalks

        then:
        assert events.size() == 110
        assert dataProvider.conference
        assert dataProvider.conference.metaData.locations.size() == 7
        assert dataProvider.conference.metaData.locations.order.join('') == ('1'..'7').join('')
        assert dataProvider.conference.metaData.tracks.size() == 8
        assert dataProvider.conference.metaData.defaultLanguage.id == 'de'
        assert dataProvider.conference.metaData.languages.size() == 2
        assert dataProvider.conference.metaData.audiences.size() == 2
    }

    void "Should return 110 events (2016) v2"() {
        when:
        dataProvider.talksUri = "resource:/javaland-2016.raw"
        Collection<Event> events = dataProvider.conference.events

        then:
        assert events.size() == 110
        assert events.location.order.unique().sort().join(', ') == (1..7).join(', ')
    }
}