package org.dukecon.server.conference

import org.dukecon.model.Conference
import org.dukecon.model.Event
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class JavalandDataRemoteSpec extends Specification {

    JavalandDataRemote dataProvider

    void setup() {
        dataProvider = new JavalandDataRemote();
        dataProvider.backup = "javaland-2016-backup.raw";
    }

    void "Should return 110 events (2016)"() {
        when:
        dataProvider.talksUri = "resource:/javaland-2016.raw"

        Conference conference = dataProvider.readConferenceData()

        then:
        assert conference.metaData.locations.size() == 7
        assert conference.metaData.locations.order.join('') == ('1'..'7').join('')
        assert conference.metaData.tracks.size() == 8
        assert conference.metaData.defaultLanguage.id == 'de'
        assert conference.metaData.languages.size() == 2
        assert conference.metaData.audiences.size() == 2
    }

    void "Should return 110 events (2016) v2"() {
        when:
        dataProvider.talksUri = "resource:/javaland-2016.raw"
        Collection<Event> events = dataProvider.readConferenceData().events

        then:
        assert events.size() == 110
        assert events.location.order.unique().sort().join(', ') == (1..7).join(', ')
    }

    void "Should return 110 events when using backup"() {
        when:
        dataProvider.talksUri = "resource:/javaland-2016.raw"
        dataProvider.readConferenceData();
        Collection<Event> events = dataProvider.readConferenceDataFallback().events;

        then:
        assert events.size() == 110
        assert dataProvider.backupActive
    }

}