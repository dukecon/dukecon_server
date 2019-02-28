package org.dukecon.server.formes2dukecon

import org.dukecon.server.conference.SpeakerImageService

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
