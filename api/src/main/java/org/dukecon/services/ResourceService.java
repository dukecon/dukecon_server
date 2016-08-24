package org.dukecon.services;

import java.util.Map;

/**
 * Created by christoferdutz on 24.08.16.
 */
public interface ResourceService {

    Map<String, Map<String, byte[]>> getResourcesForConference(String conferenceId);

}
