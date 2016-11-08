package org.dukecon.server.adapter

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
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Slf4j
@TypeChecked
class WebResourceDataProviderHealthIndicator implements HealthIndicator {

    private final WebResourceDataProvider dataProvider
    private final WebResourceDataProviderRemote dataProviderRemote

    WebResourceDataProviderHealthIndicator(WebResourceDataProvider dataProvider, WebResourceDataProviderRemote dataProviderRemote) {
        this.dataProvider = dataProvider
        this.dataProviderRemote = dataProviderRemote
    }

    @Override
    Health health() {
        try {
            // trigger loading of data and see if it succeeds
            dataProvider.conference
            if (dataProviderRemote.backupActive) {
                return new Health.Builder().down(dataProviderRemote.staleException).build();
            } else if (dataProvider.staleException) {
                return new Health.Builder().down(dataProvider.staleException).build();
            } else {
                return new Health.Builder().up().build();
            }
        } catch (Exception e) {
            return new Health.Builder().down(e).build();
        }
    }
}
