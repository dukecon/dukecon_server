package org.dukecon.server.conference.adpater

import org.dukecon.adapter.ResourceWrapper
import org.dukecon.server.repositories.RawDataResources

import org.junit.Test

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class RawDataResourcesTests {
    @Test
    void testSingleResourceAsString() {
        Map<String, ResourceWrapper> resources = RawDataResources.of('herbstcampus-2016/herbstcampus_2016_veranstaltungen_20160826.csv').get()
        assert resources.size() == 1
        assert resources.eventsData.getStream() instanceof InputStream
    }

    @Test
    void testMultipleResourcesAsMap() {
        Map<String, ResourceWrapper> resources = RawDataResources.of([eventsData: 'javaland-2016.raw', speakersData: 'javaland-speaker-2016.raw', additionalData: 'javaland-additional-2016.raw']).get()
        assert resources.size() == 3
        assert resources.eventsData.getStream() instanceof InputStream
        // TODO Rebuild without SpeakerImageService
//        assert resources.speakersData.getStream() instanceof InputStream
        assert resources.additionalData.getStream() instanceof InputStream
    }
}
