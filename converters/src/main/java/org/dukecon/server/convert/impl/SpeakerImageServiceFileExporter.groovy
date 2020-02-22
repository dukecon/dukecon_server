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
        this.service.images.forEach{ md5,image -> writeToDisk(image, this.path) }

        return pathInFilesystem.absolutePath
    }

    void writeToDisk(SpeakerImageService.ImageWithName image, String path) {
        FileOutputStream outputStream
        String fileWithPath = path + File.separator + image.filename
        try {
            outputStream = new FileOutputStream(fileWithPath)
            outputStream.write(image.content)
            outputStream.flush()
        } catch(Exception e) {
            throw new RuntimeException("could not write file: $fileWithPath ", e)
        } finally {
            if(outputStream)
                outputStream.close()
        }
    }
}

