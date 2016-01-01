package org.dukecon.server.conference

import org.dukecon.model.Event
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class JavalandDataProviderSpec extends Specification {

    JavalandDataProvider dataProvider

    void setup() {
        dataProvider = new JavalandDataProvider();
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