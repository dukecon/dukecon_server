package org.dukecon.server.conference.adpater

import org.dukecon.server.adapter.DefaultRawDataResource
import org.dukecon.server.conference.ConferencesConfiguration
import org.junit.Before
import org.junit.Test

import java.time.LocalDate

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class ConferencesConfigurationTests {
    private ConferencesConfiguration config
    private List conferences

    @Before
    public void setUp() throws Exception {
        config = ConferencesConfiguration.fromFile('conferences-test.yml', [conferenceId:'apacheconeu2016', conferenceApiKey:'foobar'])
        conferences = config.conferences
    }

    @Test
    public void parseConferencesFromYml() {
        assert conferences.size() == 2
        assert conferences.name == ['JavaLand 2016', 'ApacheCon Europe 2016']
        assert conferences[0].url == 'http://javaland.dukecon.org/2016'
        assert conferences[0].talksUri.class == String
        assert conferences[0].startDate.class == LocalDate
        assert conferences[0].extractorClass instanceof Class
        assert conferences[0].rawDataResourcesClass instanceof Class
        assert conferences[0].rawDataResourcesClass == DefaultRawDataResource.class
    }

    @Test
    public void substitutePlaceholder() throws Exception {
        assert conferences.find {it.id == 'jl2016'}.url == 'http://javaland.dukecon.org/2016'
    }

    @Test
    public void multipleRawDataResources() throws Exception {
        def apacheconEu = conferences.find {it.id == 'apacheconeu2016'}
        assert apacheconEu.talksUri instanceof Map
        if (apacheconEu.talksUri instanceof Map) {
            println apacheconEu.talksUri
        }
    }
}
