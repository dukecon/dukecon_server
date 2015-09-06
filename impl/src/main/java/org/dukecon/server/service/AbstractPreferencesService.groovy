package org.dukecon.server.service

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.UserPreference
import org.dukecon.server.business.PreferencesRepository
import org.dukecon.server.model.Preference
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @author Gerd Aschemann <gerd@aschemann.net>
 */
@Component
@Path("preferences")
@Slf4j
@TypeChecked
abstract class AbstractPreferencesService {
    @Inject
    PreferencesRepository preferencesRepository

    abstract protected String getAuthenticatedPrincipalId ()

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPreferences() {
        String principalId = getAuthenticatedPrincipalId()
        if (!principalId) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        log.debug ("Retrieving preferences for '{}'", principalId)
        Collection<Preference> preferences = preferencesRepository.findByPrincipalId (principalId)

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

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPreferences (List<UserPreference> userPreferences) {
        String principalId = getAuthenticatedPrincipalId()
        if (!principalId) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        log.debug ("Adding preferences for '{}'", principalId)
        userPreferences.each { UserPreference userPreference ->
            // Check if this preference was already created.
            Collection<Preference> preferences = preferencesRepository.findByPrincipalIdAndTalkId(
                    principalId, userPreference.talkId)
            // If it doesn't exist yet, add it.
            if((preferences == null) || preferences.empty) {
                Preference p = new Preference(principalId : principalId, talkId : userPreference.talkId)
                preferencesRepository.save(p)
            } else {
                // Well actually we tried to create something that's already
                // there ... this is actually a conflict, but not a bad one
                // let's leave it to the client do decide.
                return Response.status(Response.Status.CONFLICT).build()
            }
        }

        return Response.status(Response.Status.CREATED).build()
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removePreferences (List<UserPreference> userPreferences) {
        String principalId = getAuthenticatedPrincipalId()
        if (!principalId) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        log.debug ("Removing preferences for '{}'", principalId)
        userPreferences.each { UserPreference userPreference ->
            // Actually this should only return max one element, but I
            // don't quite understand the mechanisms behind this service.
            Collection<Preference> preferences = preferencesRepository.findByPrincipalIdAndTalkId(
                    principalId, userPreference.talkId)
            if((preferences != null) && !preferences.empty) {
                preferences.each { Preference preference ->
                    preferencesRepository.delete(preference.id)
                }
            } else {
                // If we try to remove something that wasn't found,
                // it's not a real problem, but by returning "not found"
                // the client can react on it.
                return Response.status(Response.Status.NOT_FOUND).build()
            }
        }

        return Response.status(Response.Status.OK).build()
    }

}
