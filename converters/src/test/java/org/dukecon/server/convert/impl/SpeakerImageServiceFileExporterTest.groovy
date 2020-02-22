package org.dukecon.server.convert.impl

import org.junit.Test

class SpeakerImageServiceFileExporterTest {

    @Test
    void "test write images to disk" () {
        given:
        def path = File.createTempDir().getAbsolutePath()
        def service = new DoagSpeakerImageService()
        def exporter = new SpeakerImageServiceFileExporter(service, path)
        def fileName = "abc.jpeg"
        service.addImage([] as byte[], fileName)

        when:
        def outputDir = exporter.export()

        then:
        new File(outputDir + File.separator + fileName).exists()
    }
}
