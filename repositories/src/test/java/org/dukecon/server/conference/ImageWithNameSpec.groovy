package org.dukecon.server.conference

import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class ImageWithNameSpec extends Specification {

    def "test write image to disk" () {
        given:
        def image = new SpeakerImageService.ImageWithName("abc.jpeg", [] as byte[])
        def path = File.createTempDir().getAbsolutePath()
        when:
        image.writeToDisk(path)
        then:
        new File(path + File.separator + image.filename).exists()
    }

}
