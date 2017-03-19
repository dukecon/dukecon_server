package org.dukecon.server.adapter.heise

import org.dukecon.model.Conference
import org.dukecon.server.adapter.ConferenceDataProvider

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
//@Component
class HeiseDataProvider implements ConferenceDataProvider {
    @Override
    String getConferenceId() {
        return "foobar"
    }

    @Override
    Conference getConference() {
        new HeiseDataExtractor('1234', new HeiseCsvInput('herbstcampus-2016/herbstcampus_2016_veranstaltungen_20160826.csv'), LocalDate.parse('2016-08-30', DateTimeFormatter.ofPattern("yyyy-MM-dd")), 'http://www.herbstcampus.de', 'Herbstcampus 2016').conference
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
