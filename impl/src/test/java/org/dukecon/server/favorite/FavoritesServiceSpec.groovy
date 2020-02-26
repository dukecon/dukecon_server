package org.dukecon.server.favorite


import groovy.util.logging.Slf4j
import org.dukecon.model.user.UserPreference
import org.dukecon.server.conference.AbstractDukeConSpec
import org.dukecon.server.favorites.FavoritesService
import org.dukecon.server.favorites.PreferencesServiceImpl
import org.dukecon.services.ConferenceService
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

import javax.inject.Inject
import javax.ws.rs.core.Response
import java.security.Principal

/**
 * @author Gerd Aschemann, http://aschemann.net, @GerdAschemann
 */
@Slf4j
class FavoritesServiceSpec extends AbstractDukeConSpec {
    @Inject
    PreferencesServiceImpl preferencesService

    @Inject
    private FavoritesService favoritesService

    @Inject
    private ConferenceService conferenceService

    void "test get without authorization" () {
        SecurityContextHolder.getContext().authentication = null
        when:
            Response response = preferencesService.getPreferences()
            log.debug ("Response: {}", response)
        then:
            assert Response.Status.NOT_FOUND == response.getStatusInfo()
    }

    void "test get without principal" () {
        Authentication emptyAuthentication = [ getPrincipal : { null } ] as Authentication
        SecurityContextHolder.getContext().authentication = emptyAuthentication
        when:
            Response response = preferencesService.getPreferences()
            log.debug ("Response: {}", response)
        then:
            assert Response.Status.NOT_FOUND == response.getStatusInfo()
    }

    void "test get no preferences" () {
        Principal dummyPrincipal = [ toString : {"id has no events"} ] as Principal
        Authentication dummyAuthentication = [ getPrincipal : { dummyPrincipal } ] as Authentication
        SecurityContextHolder.getContext().authentication = dummyAuthentication
        when:
            Response response = preferencesService.getPreferences()
            log.debug ("Response: {}", response)
        then:
            assert Response.Status.OK == response.getStatusInfo()
            List<UserPreference> result = response.entity as List<UserPreference>
            assert 0 == result.size()
    }

    void "test set simple preferences" () {
        Principal testerPrincipal = [ toString : {"GerdTheTester"} ] as Principal
        Authentication testerAuthentication = [ getPrincipal : { testerPrincipal } ] as Authentication
        SecurityContextHolder.getContext().authentication = testerAuthentication
        List<UserPreference> userPreferences = [
                UserPreference.builder().eventId("17").build(),
                UserPreference.builder().eventId("18").build(),
                UserPreference.builder().eventId("19").build(),
        ]
        when:
            Response responseSet = preferencesService.setPreferences(userPreferences)
            log.debug ("Response (set): {}", responseSet)
            Response responseGet = preferencesService.getPreferences()
            log.debug ("Response (get): {}", responseGet)
        then:
            assert Response.Status.CREATED == responseSet.getStatusInfo()
            assert Response.Status.OK == responseGet.getStatusInfo()
            List<UserPreference> result = (List<UserPreference>)responseGet.entity
            assert 3 == result.size()
    }

    void "test update preferences" () {
        Principal testerPrincipal = [ toString : {"FalkTheTester"} ] as Principal
        Authentication testerAuthentication = [ getPrincipal : { testerPrincipal } ] as Authentication
        SecurityContextHolder.getContext().authentication = testerAuthentication
        List<UserPreference> userPreferences = [
                UserPreference.builder().eventId("17").build(),
                UserPreference.builder().eventId("18").build(),
                UserPreference.builder().eventId("19").build(),
        ]
        List<UserPreference> updatedUserPreferences = [
                UserPreference.builder().eventId("17").version(1).build(),
                UserPreference.builder().eventId("18").build(),
                UserPreference.builder().eventId("19").build(),
        ]
        when:
        Response responseSet = preferencesService.setPreferences(userPreferences)
        log.debug ("Response (initial set): {}", responseSet)
        Response responseUpdate = preferencesService.setPreferences(updatedUserPreferences)
        log.debug ("Response (update): {}", responseUpdate)
        Response responseGet = preferencesService.getPreferences()
        log.debug ("Response (get): {}", responseGet)
        then:
        assert Response.Status.CREATED == responseSet.getStatusInfo()
        assert Response.Status.CREATED == responseUpdate.getStatusInfo()
        assert Response.Status.OK == responseGet.getStatusInfo()
        List<UserPreference> result = (List<UserPreference>)responseGet.entity
        assert 3 == result.size()
    }

    void "test delete preferences" () {
        Principal testerPrincipal = [ toString : {"NikoTheTester"} ] as Principal
        Authentication testerAuthentication = [ getPrincipal : { testerPrincipal } ] as Authentication
        SecurityContextHolder.getContext().authentication = testerAuthentication
        List<UserPreference> userPreferences = [
                UserPreference.builder().eventId("17").build(),
                UserPreference.builder().eventId("18").build(),
                UserPreference.builder().eventId("19").build(),
        ]
        List<UserPreference> updatedUserPreferences = [
                UserPreference.builder().eventId("17").version(1).build(),
                // 18 is deleted
                UserPreference.builder().eventId("19").build(),
        ]
        when:
        Response responseSet = preferencesService.setPreferences(userPreferences)
        log.debug ("Response (initial set): {}", responseSet)
        Response responseUpdate = preferencesService.setPreferences(updatedUserPreferences)
        log.debug ("Response (update): {}", responseUpdate)
        Response responseGet = preferencesService.getPreferences()
        log.debug ("Response (get): {}", responseGet)
        then:
        assert Response.Status.CREATED == responseSet.getStatusInfo()
        assert Response.Status.CREATED == responseUpdate.getStatusInfo()
        assert Response.Status.OK == responseGet.getStatusInfo()
        List<UserPreference> result = (List<UserPreference>)responseGet.entity
        assert 2 == result.size()
    }

    void "get all favorites for conference"() {
        when:
        def eventFavorites = favoritesService?.getAllFavoritesForConference(conferenceService?.getConference('javaland2017'))
        then:
        eventFavorites.size() > 0
        eventFavorites.first().track
        eventFavorites.first().type
        eventFavorites.first().speakers
        eventFavorites.first().eventId
        eventFavorites.first().location
        eventFavorites.first().locationCapacity >= 0
        eventFavorites.first().numberOfFavorites >= 0
        eventFavorites.first().start
        eventFavorites.first().title
    }
}
