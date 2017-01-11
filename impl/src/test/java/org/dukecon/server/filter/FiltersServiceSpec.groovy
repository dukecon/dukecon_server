package org.dukecon.server.filter

import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.dukecon.model.user.UserFilters
import org.dukecon.model.user.UserPreference
import org.dukecon.server.conference.AbstractDukeConSpec
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

import javax.inject.Inject
import javax.ws.rs.core.Response
import java.security.Principal

/**
 * @author Gerd Aschemann, http://aschemann.net, @GerdAschemann
 */

@Slf4j
@TypeChecked
class FiltersServiceSpec extends AbstractDukeConSpec {

    @Inject
    FiltersService service

    void "test get without authorization should succeed at the moment"() {
        SecurityContextHolder.getContext().authentication = null
        when:
        Response response = service.getFilters()
        log.debug("Response: {}", response)
        then:
        assert Response.Status.OK == response.getStatusInfo()
    }

    void "test get without principal should succeed at the moment"() {
        Authentication emptyAuthentication = [getPrincipal: { null }] as Authentication
        SecurityContextHolder.getContext().authentication = emptyAuthentication
        when:
        Response response = service.getFilters()
        log.debug("Response: {}", response)
        then:
        assert Response.Status.OK == response.getStatusInfo()
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    void "test get no filters"() {
        Principal dummyPrincipal = [toString: { "thePrincipal" }] as Principal
        Authentication dummyAuthentication = [getPrincipal: { dummyPrincipal }] as Authentication
        SecurityContextHolder.getContext().authentication = dummyAuthentication
        when:
        Response response = service.getFilters()
        log.debug("Response: {}", response)
        then:
        assert Response.Status.OK == response.getStatusInfo()
        UserFilters result = (UserFilters) response.entity
        assert result
        assert !result.favourites
        assert !result.languages
        assert !result.levels
        assert !result.locations
        assert !result.tracks
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    void "test set simple filters"() {
        Principal testerPrincipal = [toString: { "testerPrincipal" }] as Principal
        Authentication testerAuthentication = [getPrincipal: { testerPrincipal }] as Authentication
        SecurityContextHolder.getContext().authentication = testerAuthentication
        def filters = UserFilters.builder().favourites(true).languages(["Hebräisch"]).tracks(["Track 1", "Track 2"]).build()
        when:
        Response responseSet = service.saveFilter(filters)
        log.debug("Response (set): {}", responseSet)
        Response responseGet = service.getFilters()
        log.debug("Response (get): {}", responseGet)
        then:
        assert Response.Status.CREATED == responseSet.getStatusInfo()
        assert Response.Status.OK == responseGet.getStatusInfo()
        UserFilters result = responseGet.entity as UserFilters
        assert result.favourites
        assert result.languages
        assert result.tracks
        assert !result.levels
        assert !result.locations
    }

    void "test update filters"() {
        Principal testerPrincipal = [toString: { "FalkTheTester" }] as Principal
        Authentication testerAuthentication = [getPrincipal: { testerPrincipal }] as Authentication
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

        // TODO adapt to Filter and uncomment
//        when:
//        Response responseSet = service.setPreferences(userPreferences)
//        log.debug ("Response (initial set): {}", responseSet)
//        Response responseUpdate = service.setPreferences(updatedUserPreferences)
//        log.debug ("Response (update): {}", responseUpdate)
//        Response responseGet = service.getPreferences()
//        log.debug ("Response (get): {}", responseGet)
//        then:
//        assert Response.Status.CREATED == responseSet.getStatusInfo()
//        assert Response.Status.CREATED == responseUpdate.getStatusInfo()
//        assert Response.Status.OK == responseGet.getStatusInfo()
//        List<UserPreference> result = (List<UserPreference>)responseGet.entity
//        assert 3 == result.size()
    }

//    void "test delete favorites" () {
//        Principal testerPrincipal = [ toString : {"NikoTheTester"} ] as Principal
//        Authentication testerAuthentication = [ getPrincipal : { testerPrincipal } ] as Authentication
//        SecurityContextHolder.getContext().authentication = testerAuthentication
//        List<UserPreference> userPreferences = [
//                UserPreference.builder().eventId("17").build(),
//                UserPreference.builder().eventId("18").build(),
//                UserPreference.builder().eventId("19").build(),
//        ]
//        List<UserPreference> updatedUserPreferences = [
//                UserPreference.builder().eventId("17").version(1).build(),
//                // 18 is deleted
//                UserPreference.builder().eventId("19").build(),
//        ]
//        when:
//        Response responseSet = service.setPreferences(userPreferences)
//        log.debug ("Response (initial set): {}", responseSet)
//        Response responseUpdate = service.setPreferences(updatedUserPreferences)
//        log.debug ("Response (update): {}", responseUpdate)
//        Response responseGet = service.getPreferences()
//        log.debug ("Response (get): {}", responseGet)
//        then:
//        assert Response.Status.CREATED == responseSet.getStatusInfo()
//        assert Response.Status.CREATED == responseUpdate.getStatusInfo()
//        assert Response.Status.OK == responseGet.getStatusInfo()
//        List<UserPreference> result = (List<UserPreference>)responseGet.entity
//        assert 2 == result.size()
//    }
}
