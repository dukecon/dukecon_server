package org.dukecon.server.eventbooking

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.*

/**
 * Database entity for storing location utilization for a concrete event/talk.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = ['conference_id', 'event_id']))
class EventBooking {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    long id

    @Column(name = "conference_id", nullable = false)
    String conferenceId

    @Column(name = "event_id", nullable = false)
    String eventId

    /** flag indicating this event is already full or is expected to become very full */
    boolean fullyBooked = false

    /** statistic about number of attendees have been at this event */
    int numberOccupied

    /** number of people, favored this event in advance, retrieved from favorites table, will not be stored in DB, for json marshalling only */
    @Transient
    int numberOfFavorites

    /** size of this event location, retrieved from conference meta data, will not be stored in DB, for json marshalling only */
    @Transient
    int locationCapacity

    EventBooking withNumberOfFavoritesAndLocationCapacity(int numberOfFavorites, int locationCapacity) {
        this.numberOfFavorites = numberOfFavorites
        this.locationCapacity = locationCapacity
        return this
    }

}
