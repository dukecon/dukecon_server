package org.dukecon.server.conference.adpater

import org.dukecon.server.adapter.MultipleRawDataResources
import org.dukecon.server.adapter.RawDataResourceSupplier
import org.junit.Test

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class MultipleRawDataResourcesTests {
    @Test
    void testSingleResource() {
        Map<String, RawDataResourceSupplier> resources = new MultipleRawDataResources('herbstcampus-2016/herbstcampus_2016_veranstaltungen_20160826.csv').get()
        assert resources.size() == 1
        assert resources.eventsData.get() instanceof InputStream
    }

    @Test
    void testMultipleResources() {
        Map<String, RawDataResourceSupplier> resources = new MultipleRawDataResources([eventsData: 'javaland-2016.raw', speakersData: 'javaland-speaker-2016.raw']).get()
        assert resources.size() == 2
        assert resources.eventsData.get() instanceof InputStream
        assert resources.speakersData.get() instanceof InputStream
    }
}
