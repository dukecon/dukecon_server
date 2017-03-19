package org.dukecon.server.favorites

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository


/**
 * @author Gerd Aschemann, http://aschemann.net, @GerdAschemann
 */
interface PreferencesRepository extends CrudRepository<Preference, Long> {
    Collection<Preference> findByPrincipalId(String principalId)

    Collection<Preference> findByPrincipalIdAndEventId(String principalId, String eventId)

    @Query("SELECT p.eventId, count(p) FROM org.dukecon.server.favorites.Preference p group by p.eventId")
    List<Object[]> allFavoritesPerEvent()
}
