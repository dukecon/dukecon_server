package org.dukecon.server.convert.impl

import groovy.util.logging.Slf4j
import org.dukecon.server.conference.SpeakerImageService

@Slf4j
class SpeakerImageServiceFileExporter {

    private final SpeakerImageService service
    private final String path

    SpeakerImageServiceFileExporter(SpeakerImageService service, String path) {
       this.service = service
       this.path = path
    }

    String export() {
        def pathInFilesystem = new File(this.path)
        if(!pathInFilesystem.exists())
            pathInFilesystem.mkdirs()

        this.service.images.forEach{ md5,image -> writeToDisk(image) }

        return pathInFilesystem.absolutePath
    }

    private void writeToDisk(SpeakerImageService.ImageWithName image) {
        new File(this.path + File.separator + image.filename).append(image.content)
    }
}

