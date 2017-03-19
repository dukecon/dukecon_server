package org.dukecon.server.favorite

import groovy.util.logging.Slf4j
import org.dukecon.server.conference.AbstractDukeConSpec
import org.dukecon.server.favorites.Preference
import org.dukecon.server.favorites.PreferencesRepository
import org.springframework.dao.DataIntegrityViolationException

import javax.inject.Inject

/**
 * @author Gerd Aschemann, http://aschemann.net, @GerdAschemann
 */
@Slf4j
class FavoritesRepositorySpec extends AbstractDukeConSpec {
    @Inject
    PreferencesRepository preferencesRepository

    void "test simple insert" () {
        when:
            Preference savedPref = preferencesRepository.save (new Preference (principalId : "0815", eventId: "001"))
        then:
            assert null != savedPref
            log.debug ("New Preference has id {} and version {}", savedPref.id, savedPref.version)
    }

    void "test insert and retrieve" () {
        when:
            Preference savedPref = preferencesRepository.save(new Preference (principalId : "0815", eventId: "002", version : 1))
            List<Preference> results = preferencesRepository.findByPrincipalId("0815")
        then:
            assert null != savedPref
            log.debug ("New Preference has id {} and version {}", savedPref.id, savedPref.version)
            assert null != results
            assert 0 < results.size()
            log.debug ("Found #{} Preferences", results.size())
    }

    void "test constraint violation" () {
        when:
            preferencesRepository.save(new Preference (principalId : "0815", eventId: "003", version : 1))
            preferencesRepository.save(new Preference (principalId : "0815", eventId: "003", version : 1))
        then:
            DataIntegrityViolationException e = thrown()
            log.debug ("Expected exception '{}' was thrown", e.message)
    }

    void "test all favorites per event"() {
        when:
            preferencesRepository.save(new Preference (principalId : "0815", eventId: "003", version : 1))
            preferencesRepository.save(new Preference (principalId : "0815", eventId: "005", version : 1))
            preferencesRepository.save(new Preference (principalId : "4711", eventId: "005", version : 1))
        and:
            def events = preferencesRepository.allFavoritesPerEvent()
        then:
            assert events.size() == 2
            assert events.sort{it[0]}.first() == ['003', 1] as Object[]
            assert events.sort{it[0]}.first().first().class == String
            assert events.sort{it[0]}.first().last().class == Long
            assert events.sort{it[0]}.last() == ['005', 2] as Object[]
    }
}
