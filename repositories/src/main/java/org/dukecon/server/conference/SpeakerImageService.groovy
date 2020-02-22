package org.dukecon.server.conference

import org.springframework.stereotype.Service

/**
 * Created by ascheman on 17.06.17.
 */
@Service
interface SpeakerImageService {
    static class ImageWithName {
        final String filename
        final byte[] content

        ImageWithName(String filename, byte[] content) {
            this.filename = filename
            this.content = content
        }

    }

    Map<String, SpeakerImageService.ImageWithName> getImages()
    String addImage(byte[] content)
    String addImage(byte[] content, String filename)
    String addImage(String contentBase64)
    String addImage(String contentBase64, String filename)
    ImageWithName getImage(String md5Hash)
}
