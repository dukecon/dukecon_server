package org.dukecon.server.adapter

import groovy.transform.TypeChecked
import org.dukecon.model.Conference
import org.springframework.beans.factory.annotation.Value

import java.time.Instant

/**
 * Data provider for a resource file reachable through an url which may be updated periodically. For the sake of
 * resilience there will be a backup of the last successful read of the web resource.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class WebResourceDataProvider implements ConferenceDataProvider {

    private final WebResourceDataProviderRemote remote

    @Value("\${talks.cache.expires:3600}")
    Integer cacheExpiresAfterSeconds

    volatile Instant cacheLastUpdated

    volatile Conference conference;

    volatile Exception staleException;
    private final String conferenceId

    WebResourceDataProvider(WebResourceDataProviderRemote dataProviderRemote, String conferenceId) {
        this.conferenceId = conferenceId
        this.remote = dataProviderRemote
    }

    @Override
    String getConferenceId() {
        return conferenceId
    }

    @Override
    Conference getConference() {
        checkCache()
        return conference
    }

    private void checkCache() {
        if (!cacheLastUpdated || isCacheExpired()) {
            // Synchronized to avoid triggering reads in parallel
            synchronized (this) {
                if (!cacheLastUpdated || isCacheExpired()) {
                    update()
                }
            }
        }
    }

    private boolean isCacheExpired() {
        if (!cacheExpiresAfterSeconds) {
            return true
        }
        return cacheLastUpdated.plusSeconds(cacheExpiresAfterSeconds).isBefore(Instant.now())
    }

    public synchronized boolean update() {
        try {
            this.conference = remote.readConferenceData()
            staleException = null
        } catch (Exception e) {
            staleException = e
        }
        if (conference == null) {
            // no previously cached result exists
            throw staleException;
        }
        /* indepdendent of the result update the timestamp so
           the next caller will get the cached result.
         */
        cacheLastUpdated = Instant.now()
        return staleException == null && !remote.isBackupActive()
    }

    public boolean isBackupActive() {
        return remote.isBackupActive()
    }

}
