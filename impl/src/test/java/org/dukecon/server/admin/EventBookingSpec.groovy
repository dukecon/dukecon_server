package org.dukecon.server.admin

import org.dukecon.server.eventbooking.EventBooking
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class EventBookingSpec extends Specification {

    def "test full booked is false and capacity is zero"() {
        when:
        def eventBooking = new EventBooking()
        then:
        !eventBooking.fullyBooked
        !eventBooking.locationCapacity
    }

    def "test numberOccupied doesn't change when setting fully booked"() {
        when:
        def eventBooking = new EventBooking(locationCapacity: 100)
        and:
        eventBooking.fullyBooked = true
        then:
        eventBooking.fullyBooked
        eventBooking.numberOccupied == 0
    }

    def "test remove fully booked"() {
        given:
        def eventBooking = new EventBooking(locationCapacity: 100, numberOccupied: 100, fullyBooked: true)
        when:
        eventBooking.fullyBooked
        and:
        eventBooking.fullyBooked = false
        then:
        !eventBooking.fullyBooked
        eventBooking.numberOccupied == 100
    }

}
