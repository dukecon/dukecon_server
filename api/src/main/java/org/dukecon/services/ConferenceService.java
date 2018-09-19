package org.dukecon.services;

import org.dukecon.model.Conference;
import org.dukecon.model.Styles;

/**
 * Created by christoferdutz on 02.08.16.
 */
public interface ConferenceService extends CrudService<Conference> {

    Styles getConferenceStyles(String conferenceId);
    Conference getConference(String conferenceId);
    boolean refreshConference(String conferenceId);
}
