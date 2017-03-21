package org.dukecon.server.admin

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@TypeChecked
@Slf4j
class EventBookingService {

    final Set<String> fullyBooked = ConcurrentHashMap.newKeySet()

    void setFull(String eventId) {
        this.fullyBooked.add(eventId)
    }

    boolean isFull(String eventId) {
        this.fullyBooked.contains(eventId)
    }

    void removeFull(String eventId) {
        this.fullyBooked.remove(eventId)
    }
}
