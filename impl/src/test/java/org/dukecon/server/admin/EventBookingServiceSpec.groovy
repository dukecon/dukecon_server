package org.dukecon.server.admin

import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class EventBookingServiceSpec extends Specification {

    def "test empty full bookings"() {
        when:
        def service = new EventBookingService()
        then:
        service.fullyBooked.isEmpty()
        !service.isFull("foo")
    }

    def "test set full bookings"() {
        when:
        def service = new EventBookingService()
        and:
        service.setFull("12345")
        service.setFull("54321")
        then:
        !service.fullyBooked.isEmpty()
        service.isFull("12345")
        service.isFull("54321")
        !service.isFull("foo")
    }

    def "test remove full bookings"() {
        when:
        def service = new EventBookingService()
        and:
        service.setFull("12345")
        service.setFull("54321")
        then:
        service.fullyBooked.size() == 2

        when:
        service.removeFull("12345")
        then:
        service.fullyBooked.size() == 1
        and:
        service.isFull("54321")
    }
}
