package org.dukecon.server.repositories

import org.dukecon.model.Conference

/**
 * Data provider interface to abstract away different data resources providing mechanisms.
 *
 * Created by annah on 18.03.2016.
 */
interface ConferenceDataProvider {
    String getConferenceId()

    /**
     * @return from resource file created conferences
     */
    Conference getConference()

    boolean update()

    boolean isBackupActive()

    Exception staleException
}