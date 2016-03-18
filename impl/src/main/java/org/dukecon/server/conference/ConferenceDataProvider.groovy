package org.dukecon.server.conference

import org.dukecon.model.Conference
import org.springframework.stereotype.Component

/**
 * Created by annah on 18.03.2016.
 */
interface ConferenceDataProvider {

    Conference getConference();
    boolean update();
    boolean isBackupActive();
    Exception staleException;
}