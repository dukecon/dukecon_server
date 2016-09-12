package org.dukecon.server.herbstcampus

import org.dukecon.model.Conference
import org.dukecon.server.conference.ConferenceDataProvider
import org.springframework.stereotype.Component

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
class HerbstcampusDataProvider implements ConferenceDataProvider {
    @Override
    Conference getConference() {
        new HerbstcampusDataExtractor('hc16', new HerbstcampusCsvInput('herbstcampus-2016/herbstcampus_2016_veranstaltungen_20160826.csv'), '2016-08-30').conference
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
