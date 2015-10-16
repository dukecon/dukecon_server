package org.dukecon.server.business

import org.dukecon.model.Event

import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class SliceEventHelper {
    public static List<Integer> timeSlotsOf(Event event) {
        List<Event> slicedEvent = sliceEvent(event)
        return slicedEvent.start*.get(ChronoField.HOUR_OF_DAY)
    }

    public static List<Event> sliceEvent(Event event) {
        def result = []
        int slices = event.end.getHour() - event.start.getHour() + (event.end.getMinute() % 60 ? 1 : 0)
        1.upto(slices) {
            Event clone = new Event(event.properties.findAll {k,v -> k != 'class'})
            if (slices > 1) {
                clone.id = "${clone.id}-${it}"
                clone.title = "${clone.title} (${it})"
            }
            switch(it) {
                case {it == 1 && it == slices}:
                    clone.start = event.start.plus(0, ChronoUnit.HOURS)
                    clone.end = event.end.plus(0, ChronoUnit.HOURS)
                    break
                case 1:
                    clone.end = event.start.plus(it, ChronoUnit.HOURS).with {Temporal t -> t.with(ChronoField.MINUTE_OF_HOUR, 0) }
                    break
                case slices:
                    clone.start = event.start.plus(it - 1, ChronoUnit.HOURS).with {Temporal t -> t.with(ChronoField.MINUTE_OF_HOUR, 0) }
                    break
                default:
                    clone.start = event.start.plus(it - 1, ChronoUnit.HOURS).with {Temporal t -> t.with(ChronoField.MINUTE_OF_HOUR, 0) }
                    clone.end = event.start.plus(it, ChronoUnit.HOURS).with {Temporal t -> t.with(ChronoField.MINUTE_OF_HOUR, 0) }
                    break
            }
            result << clone
        }
        return result
    }

}
