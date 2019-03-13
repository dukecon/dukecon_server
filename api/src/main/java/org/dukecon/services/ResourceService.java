package org.dukecon.services;

import org.dukecon.model.CoreImages;
import org.dukecon.model.Resources;

import java.io.IOException;
import java.util.Map;

/**
 * Created by christoferdutz on 24.08.16.
 */
public interface ResourceService {

    Map<String, byte[]> getLogosForConferences();
    Resources getResourcesForConference(String conferenceId) throws IOException;
    CoreImages getCoreImagesForConference (String conferenceId) throws IOException;

}
