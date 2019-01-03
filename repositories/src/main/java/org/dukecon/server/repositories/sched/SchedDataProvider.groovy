package org.dukecon.server.repositories.sched

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.dukecon.model.Conference
import org.dukecon.server.repositories.ConferenceDataProvider
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value

import javax.inject.Inject
import java.time.Instant

/**
 * Calls the remote service and caches the result as needed.
 *
 * @deprecated will be removed in favor of WebResourceDataProvider
 *
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 * @author Christofer Dutz, christofer.dutz@codecentric.de, @ChristoferDutz
 */
@Slf4j
@TypeChecked
@Deprecated
class SchedDataProvider implements ConferenceDataProvider, InitializingBean {

    @Value("\${sched.cache.expires:3600}")
    Integer cacheExpiresAfterSeconds

    @Value("#{'\${sched.conferences:}'.split(',')}")
    private List<String> conferences

    @Inject
    private SchedDataRemote remote

    volatile Instant cacheLastUpdated

    volatile Conference conference

    volatile Exception staleException

    @Override
    void afterPropertiesSet() throws Exception {
        if(conferences.size() > 0) {
            for(String conference : conferences) {
                if(!StringUtils.isEmpty(conference)) {
                    log.info("Initializing Adapter: Sched: " + conference.split("@")[0])
                }
            }
        }
    }

    @Override
    String getConferenceId() {
        return "foobar"
    }

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

    synchronized boolean update() {
        try {
            // TODO: Greatly refactor this ...
            for(String conference : conferences) {
                if(!StringUtils.isEmpty(conference)) {
                    this.conference = remote.readConferenceData(conference)
                    staleException = null
                }
            }
        } catch (Exception e) {
            staleException = e
        }
        if((conference == null) && (staleException != null)) {
            // no previously cached result exists
            throw staleException
        }
        /* indepdendent of the result update the timestamp so
           the next caller will get the cached result.
         */
        cacheLastUpdated = Instant.now()
        return staleException == null && !remote.isBackupActive()
    }

    boolean isBackupActive() {
        return remote.isBackupActive()
    }
}
