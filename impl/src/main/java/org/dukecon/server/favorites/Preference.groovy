package org.dukecon.server.favorites

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint

/**
 * @author Gerd Aschemann, http://aschemann.net, @GerdAschemann
 */
@Entity (name = "favorites")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = ['principal_id', 'event_id']))
class Preference {
    @Id
    @GeneratedValue
    long id

    @Column(name = "principal_id", nullable = false)
    String principalId

    @Column(name = "event_id", nullable = false)
    String eventId

    @Column
    int version
}
