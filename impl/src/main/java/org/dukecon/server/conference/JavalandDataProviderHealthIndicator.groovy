package org.dukecon.server.conference

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
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
class JavalandDataProviderHealthIndicator implements HealthIndicator {

    @Inject
    JavalandDataProvider talkProvider;

    @Override
    Health health() {
        try {
            // if getAllTalks succeeds, everything is fine!
            talkProvider.getAllTalks();
            return new Health.Builder().up().build();
        } catch (Exception e) {
            return new Health.Builder().down(e).build();
        }
    }
}
