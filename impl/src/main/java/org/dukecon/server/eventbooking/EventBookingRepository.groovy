package org.dukecon.server.eventbooking

import org.springframework.data.repository.CrudRepository

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
interface EventBookingRepository extends CrudRepository<EventBooking, Long> {
    EventBooking findByConferenceIdAndEventId(String conferenceId, String eventId)
    Collection<EventBooking> findAllByConferenceId(String conferenceId)
}