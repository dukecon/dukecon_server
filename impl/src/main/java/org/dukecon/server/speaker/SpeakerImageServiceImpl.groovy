package org.dukecon.server.speaker

import groovy.util.logging.Slf4j
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.server.conference.SpeakerImageService
import org.springframework.stereotype.Service

import java.security.MessageDigest

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Service("speakerImageService")
@Slf4j
class SpeakerImageServiceImpl implements SpeakerImageService {

    Map<String, SpeakerImageService.ImageWithName> images = [:]

    String addImage(byte[] content, String filename = null) {
        return this.addImage(Base64.encoder.encodeToString(content), filename)
    }

    String addImage(String contentBase64, String filename = null) {
        // TODO: special case for JFS2017, externalize image reading (base64 + file based) to caller and just call with byte[]
        log.trace ("Adding speaker image from '{}')", filename ? "file >${filename}<" : "Image Data: >${contentBase64?.substring(0, 10)}<")
        byte[] content
        if (contentBase64.contains('.')) {
            content = ResourceWrapper.of("public/images/jfs/2017/speakers/${contentBase64}").stream?.bytes
        }

        String md5Hash = md5(contentBase64)
        images[md5Hash] = new SpeakerImageService.ImageWithName(filename ?: "${md5Hash}.${fileEnding(contentBase64)}", content ?: Base64.decoder.decode(contentBase64))
        return md5Hash
    }

    @Override
    SpeakerImageService.ImageWithName getImage(String md5Hash) {
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
