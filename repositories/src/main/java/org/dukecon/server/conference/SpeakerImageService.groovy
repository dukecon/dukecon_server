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

        void writeToDisk(String path) {
            FileOutputStream outputStream
            String fileWithPath = path + File.separator + this.filename
            try {
                outputStream = new FileOutputStream(fileWithPath)
                outputStream.write(this.content)
                outputStream.flush()
            } catch(Exception e) {
                throw new RuntimeException("could not write file: $fileWithPath ", e)
            } finally {
                if(outputStream)
                    outputStream.close()
            }
        }
    }

    Map<String, SpeakerImageService.ImageWithName> getImages()
    String addImage(byte[] content)
    String addImage(byte[] content, String filename)
    String addImage(String contentBase64)
    String addImage(String contentBase64, String filename)
    ImageWithName getImage(String md5Hash)
}
