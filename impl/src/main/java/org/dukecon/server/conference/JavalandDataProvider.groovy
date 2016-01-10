package org.dukecon.server.conference

import com.netflix.hystrix.exception.HystrixRuntimeException
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.inject.Inject
import java.time.Instant

/**
 * Calls the remote service and caches the result as needed.
 *
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Slf4j
@Component
@TypeChecked
class JavalandDataProvider {

    @Inject
    JavalandDataRemote remote;

    @Value("\${talks.cache.expires:3600}")
    Integer cacheExpiresAfterSeconds

    volatile Instant cacheLastUpdated

    volatile Conference conference;

    volatile Exception staleException;

    Conference getConference() {
        checkCache()
        return conference
    }

    private void checkCache() {
        if (!cacheLastUpdated || isCacheExpired()) {
            // Synchronized to avoid triggering reads in parallel
            synchronized (this) {
                if (!cacheLastUpdated || isCacheExpired()) {
                    try {
                        this.conference = remote.readConferenceData()
                        staleException = null
                    } catch (Exception e) {
                        staleException = e
                    }
                    if(conference == null) {
                        // no previously cached result exists
                        throw staleException;
                    }
                    /* indepdendent of the result update the timestamp so
                       the next caller will get the cached result.
                     */
                    cacheLastUpdated = Instant.now()
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

}
