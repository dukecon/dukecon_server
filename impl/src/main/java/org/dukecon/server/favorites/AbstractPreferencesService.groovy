package org.dukecon.server.favorites

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.user.UserPreference
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response;

/**
 * @author Gerd Aschemann <gerd@aschemann.net>
 */
@Component
@Slf4j
@TypeChecked
abstract class AbstractPreferencesService implements PreferencesService {

    @Value("\${preferences.noauth.enableIdParam:false}")
    private boolean enableIdParam

    @Inject
    FavoritesRepository preferencesRepository

    abstract protected String getAuthenticatedPrincipalId()

    @GET
    public Response getPreferences() {
        return getPreferences(null)
    }

    @GET
    @Path("/{id}")
    public Response getPreferences(@PathParam("id") String id) {
        String principalId = getAuthenticatedPrincipalId()
        if (!principalId) {
            if (!enableIdParam) {
                return Response.status(Response.Status.NOT_FOUND).build()
            } else if (!id) {
                return Response.status(Response.Status.BAD_REQUEST).build()
            } else {
                principalId = id
            }
        }

        log.debug("Retrieving preferences for '{}'", principalId)
        Collection<Preference> preferences = preferencesRepository.findByPrincipalId(principalId)

        Collection<UserPreference> result = []
        preferences.each { Preference p ->
            UserPreference up = UserPreference.builder().eventId(p.eventId).version(p.version).build()
            result.add(up)
        }

        return Response.ok(result).build()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setPreferences(List<UserPreference> userPreferences) {
        return setPreferences(null, userPreferences)

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response setPreferences(@PathParam("id") String id, List<UserPreference> userPreferences) {
        String principalId = getAuthenticatedPrincipalId()
        if (!principalId) {
            if (!enableIdParam) {
                return Response.status(Response.Status.NOT_FOUND).build()
            } else if (!id) {
                return Response.status(Response.Status.BAD_REQUEST).build()
            } else {
                principalId = id
            }

        }

        // Retrieve existing preferences from DB
        log.debug("Setting/Updating preferences for '{}'", principalId)
        Collection<Preference> preferences = preferencesRepository.findByPrincipalId(principalId)

        // Prepare some maps for adding/updating/deletion
        Map<String, Preference> preferencesByTalk = preferences.collectEntries { Preference p -> [p.eventId, p] }
        Map<String, UserPreference> userPreferencesByTalk = userPreferences.collectEntries { UserPreference up -> [up.eventId, up] }

        // Delete some preferences
        // Yes, we could have done so in the collector above but wanted to separate setup of
        // internal data structures from business logic
        preferences.each { Preference p ->
            if (!userPreferencesByTalk.containsKey(p.eventId)) {
                log.debug("Deleting talk {} from preferences of user {}", p.eventId, principalId)
                preferences.remove(p.eventId)
                preferencesRepository.deleteById(p.id)
            }
        }

        // Add new userPreferences and update existing ones
        userPreferences.each { UserPreference up ->
            Preference p = preferencesByTalk[up.eventId] ?: new Preference(principalId: principalId, eventId: up.eventId)
            preferencesRepository.save(p)
        }

        return Response.status(Response.Status.CREATED).build()
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPreference(UserPreference userPreference) {
        return addPreference(null, userPreference)
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response addPreference(@PathParam("id") String id, UserPreference userPreference) {
        String principalId = getAuthenticatedPrincipalId()
        if (!principalId) {
            if (!enableIdParam) {
                return Response.status(Response.Status.NOT_FOUND).build()
            } else if (!id) {
                return Response.status(Response.Status.BAD_REQUEST).build()
            } else {
                principalId = id
            }
        }

        log.debug("Adding preferences for '{}'", principalId)

        // Check if this preference was already created.
        Collection<Preference> preferences = preferencesRepository.findByPrincipalIdAndEventId(
                principalId, userPreference.eventId)
        // If it doesn't exist yet, add it.
        if ((preferences == null) || preferences.empty) {
            Preference p = new Preference(principalId: principalId, eventId: userPreference.eventId)
            preferencesRepository.save(p)
        } else {
            // Well actually we tried to create something that's already
            // there ... this is actually a conflict, but not a bad one
            // let's leave it to the client do decide.
            return Response.status(Response.Status.CONFLICT).build()
        }

        return Response.status(Response.Status.CREATED).build()
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removePreference(UserPreference userPreference) {
        return removePreference(null, userPreference)
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response removePreference(@PathParam("id") String id, UserPreference userPreference) {
        String principalId = getAuthenticatedPrincipalId()
        if (!principalId) {
            if (!enableIdParam) {
                return Response.status(Response.Status.NOT_FOUND).build()
            } else if (!id) {
                return Response.status(Response.Status.BAD_REQUEST).build()
            } else {
                principalId = id
            }
        }

        log.debug("Removing preferences for '{}'", principalId)

        // Actually this should only return max one element, but I
        // don't quite understand the mechanisms behind this service.
        Collection<Preference> preferences = preferencesRepository.findByPrincipalIdAndEventId(
                principalId, userPreference.eventId)
        if ((preferences != null) && !preferences.empty) {
            preferences.each { Preference preference ->
                preferencesRepository.deleteById(preference.id)
            }
        } else {
            // If we try to remove something that wasn't found,
            // it's not a real problem, but by returning "not found"
            // the client can react on it.
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        return Response.status(Response.Status.OK).build()
    }

    @Override
    Map<String, Integer> getAllEventFavorites() {
        return preferencesRepository.allFavoritesPerEvent().collectEntries { Object[] event ->
            [(event.first()): event.last()]
        }
    }

}
