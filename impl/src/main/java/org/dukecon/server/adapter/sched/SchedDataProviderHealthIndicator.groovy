package org.dukecon.server.adapter.sched

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.server.javaland.JavalandDataProvider
import org.dukecon.server.javaland.JavalandDataRemote
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

import javax.inject.Inject

/**
 * @author Alexander Schwartz, alexander.schwartz@gmx.net, @ahus1de
 */
@Slf4j
@Component
@TypeChecked
class SchedDataProviderHealthIndicator implements HealthIndicator {

    @Inject
    JavalandDataProvider talkProvider;

    @Inject
    JavalandDataRemote talkRemote;

    @Override
    Health health() {
        try {
            // trigger loading of data and see if it succeeds
            talkProvider.conference
            if (talkRemote.backupActive) {
                return new Health.Builder().down(talkRemote.staleException).build();
            } else if (talkProvider.staleException) {
                return new Health.Builder().down(talkProvider.staleException).build();
            } else {
                return new Health.Builder().up().build();
            }
        } catch (Exception e) {
            return new Health.Builder().down(e).build();
        }
    }
}
