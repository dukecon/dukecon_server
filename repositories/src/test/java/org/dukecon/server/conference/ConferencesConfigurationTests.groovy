package org.dukecon.server.conference

import org.dukecon.adapter.ResourceWrapper
import org.junit.Before
import org.junit.Test
import sun.reflect.annotation.ExceptionProxy

import java.time.LocalDate

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class ConferencesConfigurationTests {
    private ConferencesConfiguration config
    private List<ConferencesConfiguration.Conference> conferences

    @Before
    public void setUp() throws Exception {
        config = ConferencesConfiguration.fromFile('conferences-test.yml',
                [conferenceId:'acna2018', conferenceApiKey:'foobar'])
        conferences = config.conferences
    }

    @Test
    public void parseConferencesFromYml() {
        assert conferences.size() == 3
        assert conferences.name == ['JavaLand 2016', 'Herbstcampus 2016', 'ApacheCon North America 2018']
        assert conferences[0].url == 'http://javaland.dukecon.org/2016'
        assert conferences[0].talksUri instanceof Map
        assert conferences[0].startDate.class == LocalDate
        assert conferences[0].extractorClass instanceof Class
        assert conferences[0].rawDataResourcesClass instanceof Class
        assert conferences[0].rawDataResourcesClass == ResourceWrapper.class
    }

    @Test
    public void substitutePlaceholder() throws Exception {
        assert conferences.find {it.id == 'javaland2016'}.url == 'http://javaland.dukecon.org/2016'
    }

    @Test
    public void multipleRawDataResourcesJavaLand() throws Exception {
        def conference = conferences.find {it.id == 'javaland2016'}
        assert conference.talksUri instanceof Map
        assert conference.talksUri.eventsData == 'javaland-2016.raw'
        assert conference.talksUri.speakersData == 'javaland-speaker-2016.raw'
        assert !conference.isRemoteTalksUri()
    }

    @Test
    public void singleRawDataResourceHerbstcampus() throws Exception {
        def conference = conferences.find {it.id == 'hc2016'}
        assert conference.talksUri instanceof String
        assert !conference.isRemoteTalksUri()
    }

    @Test
    public void multipleRawDataResourcesApacheCon() throws Exception {
        def conference = conferences.find {it.id == 'acna2018'}
        assert conference.talksUri instanceof Map
        assert conference.isRemoteTalksUri()
    }

    @Test(expected = IllegalStateException.class)
    public void emptyConferenceListWhenYamlIsBroken() {
        config = ConferencesConfiguration.fromFile('conferences-test-broken.yml', [:])
    }
}
