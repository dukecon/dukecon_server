package org.dukecon.server.adapter.sched

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

import javax.inject.Inject

/**
 * @author Alexander Schwartz, alexander.schwartz@gmx.net, @ahus1de
 * @author Christofer Dutz, christofer.dutz@codecentric.de, @ChristoferDutz
 *
 * @deprecated will be removed in favor of WebResourceDataProviderHealthIndicator
 */
@Slf4j
//@Component
@TypeChecked
@Deprecated
class SchedDataProviderHealthIndicator implements HealthIndicator {

    @Inject
    SchedDataProvider talkProvider

    @Inject
    SchedDataRemote talkRemote

    @Override
    Health health() {
        try {
            // trigger loading of data and see if it succeeds
            talkProvider.conference
            if (talkRemote.backupActive) {
                return new Health.Builder().down(talkRemote.staleException).build()
            } else if (talkProvider.staleException) {
                return new Health.Builder().down(talkProvider.staleException).build()
            } else {
                return new Health.Builder().up().build()
            }
        } catch (Exception e) {
            return new Health.Builder().down(e).build()
        }
    }
}
