package org.dukecon.server.conference.adpater

import org.dukecon.server.conference.ConferencesConfiguration
import org.junit.Test

import java.time.LocalDate

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class ConferencesConfigurationTests {

    @Test
    public void parseConferencesFromYml() {
        List conferences = ConferencesConfiguration.fromFile('conferences.yml').conferences
        assert conferences.size() == 4
        assert conferences.name == ['JavaLand 2016', 'Java Forum Stuttgart 2016', 'Herbstcampus 2016', 'JavaLand 2017']
        println conferences.url
        assert conferences[0].startDate.class == LocalDate
    }
}
