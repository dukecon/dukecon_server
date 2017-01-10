package org.dukecon.server.adapter.doag

import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.dukecon.server.adapter.DefaultRawDataResource
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.xml.bind.DatatypeConverter
import java.security.MessageDigest

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Service
class DoagSpeakerImageService {

    Map<String, ImageWithName> images

    static class ImageWithName {
        final String filename
        final byte[] content

        ImageWithName(String filename, byte[] content) {
            this.filename = filename
            this.content = content
        }
    }

    @PostConstruct
    void init() {
        def speaker = new JsonSlurper().parse(new DefaultRawDataResource('javaland-speaker-2016.raw').get(), "ISO-8859-1").hits.hits._source
        images = speaker.findAll { it.PROFILFOTO }.PROFILFOTO.collectEntries {
            String md5Hash = md5(it)
            [(md5Hash): new ImageWithName("${md5Hash}.${fileEnding(it)}", DatatypeConverter.parseBase64Binary(it))]
        }
    }

    ImageWithName getImage(String md5Hash) {
        images.get(md5Hash)
    }

    private md5(String s) {
        MessageDigest digest = MessageDigest.getInstance("MD5")
        digest.update(s.bytes);
        new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
    }

    private fileEnding(String content) {
        switch (content) {
            case ~/^iVBO.*/:
                return 'png'
            case ~/^R0.*/:
                return 'gif'
            case ~$/^/9j/*/$:
            default:
                return 'jpg'
        }
    }
}
