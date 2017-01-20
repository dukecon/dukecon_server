package org.dukecon.server.conference

import org.dukecon.model.Event
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import static org.dukecon.server.conference.SliceEventHelper.*


/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class SliceEventHelperSpec extends Specification {

    void "should get time slots of event"() {
        when:
        def _08_50 = LocalDateTime.parse('2016-03-09 08:50:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def _09_00 = LocalDateTime.parse('2016-03-09 09:00:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def _11_00 = LocalDateTime.parse('2016-03-09 11:00:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def _11_10 = LocalDateTime.parse('2016-03-09 11:10:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def _11_50 = LocalDateTime.parse('2016-03-09 11:50:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def _12_00 = LocalDateTime.parse('2016-03-09 12:00:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def _14_10 = LocalDateTime.parse('2016-03-09 14:10:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def events1 = timeSlotsOf(new Event(id:"1", start: _09_00, end: _11_00))
        def events2 = timeSlotsOf(new Event(id:"1", start: _09_00, end: _14_10))
        def events3 = timeSlotsOf(new Event(id:"1", start: _08_50, end: _11_00))
        def events4 = timeSlotsOf(new Event(id:"1", start: _08_50, end: _11_10))
        def events5 = timeSlotsOf(new Event(id:"1", start: _11_10, end: _11_10))
        def events6 = timeSlotsOf(new Event(id:"1", start: _11_10, end: _11_50))
        def events7 = timeSlotsOf(new Event(id:"1", start: _11_00, end: _12_00))
        then:
        assert events1.join(', ') == '9, 10'
        assert events2.join(', ') == '9, 10, 11, 12, 13, 14'
        assert events3.join(', ') == '8, 9, 10'
        assert events4.join(', ') == '8, 9, 10, 11'
        assert events5.join(', ') == '11'
        assert events6.join(', ') == '11'
        assert events7.join(', ') == '11'
    }

    @Unroll
    def "should get time slots of event"(String id, String startTime, String endTime, String result) {
        expect:
        result == timeSlotsOf(new Event(id: id,
                start: LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                end: LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))).join(', ')

        where:
        id | startTime             | endTime               | result
        1  | '2016-03-09 09:00:00' | '2016-03-09 11:00:00' | '9, 10'
        1  | '2016-03-09 09:00:00' | '2016-03-09 14:10:00' | '9, 10, 11, 12, 13, 14'
        1  | '2016-03-09 08:50:00' | '2016-03-09 11:00:00' | '8, 9, 10'
        1  | '2016-03-09 08:50:00' | '2016-03-09 11:10:00' | '8, 9, 10, 11'
        1  | '2016-03-09 11:10:00' | '2016-03-09 11:10:00' | '11'
        1  | '2016-03-09 11:10:00' | '2016-03-09 11:50:00' | '11'
        1  | '2016-03-09 11:00:00' | '2016-03-09 12:00:00' | '11'
    }

    void "shouldPartDurationsOfMoreThanOneHour"() {
        when:
        def _08_50 = LocalDateTime.parse('2016-03-09 08:50:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def _09_00 = LocalDateTime.parse('2016-03-09 09:00:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def _11_00 = LocalDateTime.parse('2016-03-09 11:00:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def _11_10 = LocalDateTime.parse('2016-03-09 11:10:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def _11_50 = LocalDateTime.parse('2016-03-09 11:50:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def _12_00 = LocalDateTime.parse('2016-03-09 12:00:00', DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def events1 = sliceEvent(new Event(id:"1", start: _09_00, end: _11_00))
        def events2 = sliceEvent(new Event(id:"1", start: _09_00, end: _11_10))
        def events3 = sliceEvent(new Event(id:"1", start: _08_50, end: _11_00))
        def events4 = sliceEvent(new Event(id:"1", start: _08_50, end: _11_10))
        def events5 = sliceEvent(new Event(id:"1", start: _11_10, end: _11_10))
        def events6 = sliceEvent(new Event(id:"1", start: _11_10, end: _11_50))
        def events7 = sliceEvent(new Event(id:"1", start: _11_00, end: _12_00))
        then:
        assertEvents(events1, 2, '9, 10', '10, 11')
        assertEvents(events2, 3, '9, 10, 11', '10, 11, 11')
        assertEvents(events3, 3, '8, 9, 10', '9, 10, 11')
        assertEvents(events4, 4, '8, 9, 10, 11', '9, 10, 11, 11')
        assertEvents(events5, 1, '11', '11')
        assertEvents(events6, 1, '11', '11')
        assertEvents(events7, 1, '11', '12')
    }

    private void assertEvents(List<Event> events, int expectedSize, String expectedStartHours, String expectedEndHours) {
        assert events.size() == expectedSize
        assert events.id == (expectedSize > 1 ? (1..expectedSize).collect {"1-$it"} : ['1'])
        events.title.eachWithIndex {t, idx -> assert (expectedSize > 1 ? t.endsWith("(${idx + 1})") : true) }
        assert events.start.hour.join(', ') == expectedStartHours
        assert events.end.hour.join(', ') == expectedEndHours
    }

}