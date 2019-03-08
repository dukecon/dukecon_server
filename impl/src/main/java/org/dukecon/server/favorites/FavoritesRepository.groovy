package org.dukecon.server.favorites

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


/**
 * @author Gerd Aschemann, http://aschemann.net, @GerdAschemann
 */
interface FavoritesRepository extends CrudRepository<Preference, Long> {
    Collection<Preference> findByPrincipalId(String principalId)

    Collection<Preference> findByPrincipalIdAndEventId(String principalId, String eventId)

    @Query("select new org.dukecon.server.favorites.EventFavorites(fav.eventId, count(fav)) from org.dukecon.server.favorites.Preference fav where fav.eventId in :eventIds group by fav.eventId order by count(fav) desc")
    List<EventFavorites> getAllFavoritesPerEvent(@Param("eventIds") List<String> eventIds);

    // TODO: wird im Moment genutzt für die Anzeige der Favoriten je Vortrag, es werden aber immer alle geladen
    @Deprecated
    @Query("SELECT p.eventId, count(p) FROM org.dukecon.server.favorites.Preference p group by p.eventId")
    List<Object[]> allFavoritesPerEvent()
}
