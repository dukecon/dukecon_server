package org.dukecon.server.convert.impl

import groovy.util.logging.Slf4j
import org.dukecon.server.conference.SpeakerImageService

import java.security.MessageDigest

@Slf4j
class DoagSpeakerImageService implements SpeakerImageService {

	Map<String, ImageWithName> images = [:]

	String addImage(byte[] content, String filename = null) {
		return this.addImage(Base64.encoder.encodeToString(content), filename)
	}

	String addImage(String contentBase64, String filename = null) {
		log.trace ("Adding speaker image from '{}')", filename ? "file >${filename}<" : "Image Data: >${contentBase64?.substring(0, 10)}<")
		String md5Hash = md5(contentBase64)
		images[md5Hash] = new ImageWithName("${md5Hash}", Base64.decoder.decode(contentBase64))
		return md5Hash
	}

	@Override
	SpeakerImageService.ImageWithName getImage(String md5Hash) {
		images.get(md5Hash)
	}

	private md5(String s) {
		MessageDigest digest = MessageDigest.getInstance("MD5")
		digest.update(s.bytes)
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
