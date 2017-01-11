package org.dukecon.server.conference.adpater.doag

import org.apache.commons.io.FileUtils
import org.dukecon.server.adapter.doag.DoagSpeakerImageService
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class SpeakerImageServiceTests {

    DoagSpeakerImageService service

    @Before
    void init() {
        service = new DoagSpeakerImageService()
        service.init()
    }

    @Test
    void testLoadImages() {
        assert service.images.size() == 5
    }

    /**
     * Saves images from json as files in sub directory tmp.
     */
    @Test
    @Ignore
    void testStoreImages() {
        assert service.images.each { k, v ->
            FileUtils.writeByteArrayToFile(new File("tmp/${v.filename}"), v.content)
        }
    }
}
