package org.dukecon.server.eventbooking

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.services.ConferenceService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@TypeChecked
@Slf4j
@Transactional
class EventBookingService {

    private final EventBookingRepository repository
    private final ConferenceService conferenceService

    @Inject
    EventBookingService(EventBookingRepository repository, ConferenceService conferenceService) {
        this.conferenceService = conferenceService
        this.repository = repository
    }

    /**
     *
     * @param conferenceId
     * @param eventId
     * @param capacity information about number of occupied seats and fully booked
     * @return true if dataset is created and false when updated
     */
    boolean setCapacity(String conferenceId, String eventId, EventBookingResource.EventCapacityInput capacity) {
        def eventBooking = repository.findByConferenceIdAndEventId(conferenceId, eventId)
        boolean result = eventBooking as boolean
        eventBooking = eventBooking ?: new EventBooking(conferenceId: conferenceId, eventId: eventId)
        eventBooking.numberOccupied = capacity.numberOccupied ?: 0
        eventBooking.fullyBooked = capacity.fullyBooked ?: false
        eventBooking.locationCapacity = conferenceService.read(conferenceId)?.events?.find { e -> e.id == eventId }?.location?.capacity ?: 250
        repository.save(eventBooking)
        return result
    }

    Collection<EventBooking> getAllCapacities(String conferenceId) {
        return repository.findAllByConferenceId(conferenceId)
    }

    EventBooking getCapacity(String conferenceId, String eventId) {
        return repository.findByConferenceIdAndEventId(conferenceId, eventId)
    }
}
