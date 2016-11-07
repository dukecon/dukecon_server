package org.dukecon.server.adapter

import groovy.transform.TypeChecked
import org.dukecon.model.Conference

/**
 * Data provider for a resource file reachable through an url which may be updated periodically. For the sake of
 * resilience there will be a backup of the last successful read of the web resource.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class WebResourceDataProvider implements ConferenceDataProvider {
    @Override
    Conference getConference() {
        return null
    }

    @Override
    boolean update() {
        return false
    }

    @Override
    boolean isBackupActive() {
        return false
    }
}
