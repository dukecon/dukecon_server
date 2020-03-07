package org.dukecon.server.conference

import java.nio.file.Path

/**
 * Created by ascheman on 17.06.17.
 */
interface ConferencesConfigurationService {
    void init()
    List<ConferencesConfiguration.Conference> getConferences()
    ConferencesConfiguration.Conference getConference(String conference, String year)
    ConferencesConfiguration.Conference getConference(String conferenceId)
    String getBackupDir()

    void reloadInputFile(Path file)
}
