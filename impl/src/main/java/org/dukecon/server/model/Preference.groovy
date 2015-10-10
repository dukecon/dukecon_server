package org.dukecon.server.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * @author Gerd Aschemann, http://aschemann.net, @GerdAschemann
 */
@Entity (name = "preferences")
class Preference {
    @Id
    @GeneratedValue
    long id

    @Column(name = "principal_id", nullable = false)
    String principalId

    @Column(name = "talk_id", nullable = false)
    String eventId

    @Column
    int version
}
