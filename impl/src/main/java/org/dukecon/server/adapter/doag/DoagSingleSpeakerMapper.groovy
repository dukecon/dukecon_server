package org.dukecon.server.adapter.doag

import org.dukecon.model.Speaker

import java.security.MessageDigest

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagSingleSpeakerMapper {
    final Speaker speaker

    DoagSingleSpeakerMapper(input) {
        this.speaker = Speaker.builder()
                .id(input.ID_PERSON?.toString())
                .name("${input.VORNAME} ${input.NACHNAME}")
                .firstname(input.VORNAME)
                .lastname(input.NACHNAME)
                .website(input.WEBSEITE)
                .company(input.FIRMA)
//                .email(input.)
                .twitter(input.LINKTWITTER)
//                .gplus(input.)
                .facebook(input.LINKFACEBOOK)
                .xing(input.LINKXING)
                .linkedin(input.LINKEDIN)
                .bio(input.PROFILTEXT)
                .photoId(md5(input.PROFILFOTO))
                .build()
    }

    private md5(String s) {
        if (!s) {
            return null
        }
        MessageDigest digest = MessageDigest.getInstance("MD5")
        digest.update(s.bytes);
        new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
    }

}
