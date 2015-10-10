package org.dukecon.server.business

import org.dukecon.server.model.Preference
import org.springframework.data.repository.CrudRepository

/**
 * @author Gerd Aschemann, http://aschemann.net, @GerdAschemann
 */
interface PreferencesRepository extends CrudRepository<Preference,Long> {
    Collection<Preference> findByPrincipalId(String principalId)
    Collection<Preference> findByPrincipalIdAndEventId(String principalId, String eventId)
}
