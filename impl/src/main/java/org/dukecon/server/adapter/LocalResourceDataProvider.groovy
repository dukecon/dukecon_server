package org.dukecon.server.adapter

import groovy.transform.TypeChecked
import org.dukecon.model.Conference

/**
 * Data provider for a resource file located in the application archive. There will be no backups and automatic
 * updates. For changes the resource file needs to be replaced and a restart/redeployment has to happen.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class LocalResourceDataProvider implements ConferenceDataProvider {

    private final ConferenceDataExtractor extractor
    private final String conferenceId

    LocalResourceDataProvider(ConferenceDataExtractor extractor, String conferenceId) {
        this.conferenceId = conferenceId
        this.extractor = extractor
    }

    @Override
    String getConferenceId() {
        return conferenceId
    }

    @Override
    Conference getConference() {
        return extractor.conference
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
