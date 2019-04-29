package org.dukecon.server.repositories.heise

import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.dukecon.server.repositories.ConferenceDataProvider

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
//@Component
@Slf4j
class HeiseDataProvider implements ConferenceDataProvider {
    @Override
    String getConferenceId() {
        return "foobar"
    }

    @Override
    Conference getConference() {
//        new HeiseDataExtractor('1234', new HeiseCsvInput('herbstcampus-2016/herbstcampus_2016_veranstaltungen_20160826.csv'), LocalDate.parse('2016-08-30', DateTimeFormatter.ofPattern("yyyy-MM-dd")), 'http://www.herbstcampus.de', 'Herbstcampus 2016').conferences
        log.error ("This method should never be called")
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
