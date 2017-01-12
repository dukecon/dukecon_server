package org.dukecon.server.conference.adpater

import org.dukecon.adapter.ResourceWrapper

import org.dukecon.server.conference.ConferencesConfiguration
import org.junit.Before
import org.junit.Test

import java.time.LocalDate

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class ConferencesConfigurationTests {
    private ConferencesConfiguration config
    private List<ConferencesConfiguration.Conference> conferences

    @Before
    public void setUp() throws Exception {
        config = ConferencesConfiguration.fromFile('conferences-test.yml', [conferenceId:'apacheconeu2016', conferenceApiKey:'foobar'])
        conferences = config.conferences
    }

    @Test
    public void parseConferencesFromYml() {
        assert conferences.size() == 3
        assert conferences.name == ['JavaLand 2016', 'Herbstcampus 2016', 'ApacheCon Europe 2016']
        assert conferences[0].url == 'http://javaland.dukecon.org/2016'
        assert conferences[0].talksUri instanceof Map
        assert conferences[0].startDate.class == LocalDate
        assert conferences[0].extractorClass instanceof Class
        assert conferences[0].rawDataResourcesClass instanceof Class
        assert conferences[0].rawDataResourcesClass == ResourceWrapper.class
    }

    @Test
    public void substitutePlaceholder() throws Exception {
        assert conferences.find {it.id == 'jl2016'}.url == 'http://javaland.dukecon.org/2016'
    }

    @Test
    public void multipleRawDataResourcesJavaLand() throws Exception {
        def confernece = conferences.find {it.id == 'jl2016'}
        assert confernece.talksUri instanceof Map
        assert confernece.talksUri.eventsData == 'javaland-2016.raw'
        assert confernece.talksUri.speakersData == 'javaland-speaker-2016.raw'


    }

    @Test
    public void singleRawDataResourceHerbstcampus() throws Exception {
        def conference = conferences.find {it.id == 'hc2016'}
        assert conference.talksUri instanceof String
    }
}
