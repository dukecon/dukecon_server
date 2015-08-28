package org.dukecon.server.service

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.UserPreference
import org.dukecon.server.business.PreferencesRepository
import org.dukecon.server.model.Preference
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @author Gerd Aschemann <gerd@aschemann.net>
 */
@Component
@Path("preferences")
@Slf4j
@TypeChecked
class PreferencesService {
    @Inject
    PreferencesRepository preferencesRepository

    private static String getAuthenticatedPrincipalId () {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth) {
            log.error ("There is no authentication given")
            return null
        }

        Object principal = auth.principal
        if (!principal) {
            log.error ("The authentication '{}' does not contain a valid principal", auth)
            return null
        }
        String principalId = principal.toString()

        return principalId
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPreferences() {
        String principalId = getAuthenticatedPrincipalId()
        if (!principalId) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        log.debug ("Retrieving preferences for '{}'", principalId)
        Collection<Preference> preferences = preferencesRepository.findByPrincipalId (principalId)
        if (preferences.size() == 0) {
            return Response.noContent().build()
        }

        Collection<UserPreference> result = []
        preferences.each {Preference p ->
            UserPreference up = UserPreference.builder().talkId(p.talkId).version(p.version).build()
            result.add (up)
        }

        return Response.ok(result).build()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setPreferences (List<UserPreference> userPreferences) {
        String principalId = getAuthenticatedPrincipalId()
        if (!principalId) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        // Retrieve existing preferences from DB
        log.debug ("Setting/Updating preferences for '{}'", principalId)
        Collection<Preference> preferences = preferencesRepository.findByPrincipalId (principalId)

        // Prepare some maps for adding/updating/deletion
        Map<String, Preference> preferencesByTalk = preferences.collectEntries {Preference p -> [p.talkId, p]}
        Map<String, UserPreference> userPreferencesByTalk = userPreferences.collectEntries {UserPreference up -> [up.talkId, up]}

        // Delete some preferences
        // Yes, we could have done so in the collector above but wanted to separate setup of
        // internal data structures from business logic
        preferences.each {Preference p ->
            if (!userPreferencesByTalk.containsKey(p.talkId)) {
                log.debug ("Deleting talk {} from preferences of user {}", p.talkId, principalId)
                preferences.remove(p.talkId)
                preferencesRepository.delete(p.id)
            }
        }

        // Add new userPreferences and update existing ones
        userPreferences.each {UserPreference up->
            Preference p = preferencesByTalk[up.talkId] ?: new Preference(principalId : principalId, talkId : up.talkId)
            p = preferencesRepository.save(p)
        }

        return Response.status(Response.Status.CREATED).build()
    }
}
