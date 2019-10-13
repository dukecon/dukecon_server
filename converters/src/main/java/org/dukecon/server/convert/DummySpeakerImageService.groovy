package org.dukecon.server.convert

import org.dukecon.server.conference.SpeakerImageService

/**
 * Will be obsolete as soon as conference provider will be disabled in dukecon_server implementation. This interface and
 * its references can be removed when speaker images will be saved as static files.
 */
@Deprecated
class DummySpeakerImageService implements SpeakerImageService {
    @Override
    Map<String, ImageWithName> getImages() {
        return null
    }

    @Override
    String addImage(byte ... content) {
        return null
    }

    @Override
    String addImage(byte[] content, String filename) {
        return null
    }

    @Override
    String addImage(String contentBase64) {
        return null
    }

    @Override
    String addImage(String contentBase64, String filename) {
        return null
    }

    @Override
    ImageWithName getImage(String md5Hash) {
        return null
    }
}
