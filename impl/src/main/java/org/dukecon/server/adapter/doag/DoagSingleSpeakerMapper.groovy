package org.dukecon.server.adapter.doag

import org.dukecon.model.Speaker

import java.security.MessageDigest

/**
 * Maps a single speaker from json input map to @{@link Speaker}.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagSingleSpeakerMapper {
    final Speaker speaker

    /**
     * Different mapping source types of DOAG speaker information.
     */
    enum Type {
        /** information from speaker export */
        DEFAULT,
        /** main (1st) speaker information from talk export */
        REFERENT('REFERENT_'),
        /** co (2nd) speaker information from talk export */
        COREFERENT('COREFERENT_', '_COREF'),
        /** co co (3rd) speaker information from talk export */
        COCOREFERENT('COCOREFERENT_', '_COCOREF');

        private final String namesSuffix
        private final String idPrefix

        private Type(String namesSuffix = '', String idPrefix = '') {
            this.idPrefix = idPrefix
            this.namesSuffix = namesSuffix
        }

        String getIdKey() {"ID_PERSON${this.idPrefix}"}
        String getNameKey() {"${this.namesSuffix}NAME"}
        String getFirstnameKey() {"${this.namesSuffix}VORNAME"}
        String getLastnameKey() {"${this.namesSuffix}NACHNAME"}
        String getCompanyKey() {"${this.namesSuffix}FIRMA"}
    }

    DoagSingleSpeakerMapper(input) {
        this(input, Type.DEFAULT)
    }

    DoagSingleSpeakerMapper(input, Type type) {
        this.speaker = input[type.idKey] ? Speaker.builder()
                .id(input[type.idKey]?.toString())
                .name(input[type.nameKey] ?: "${input.VORNAME ?: ''} ${input[type.lastnameKey] ?: ''}".trim())
                .firstname(input.VORNAME ?: input[type.lastnameKey] ? (input[type.nameKey] - input[type.lastnameKey]).trim() : input[type.nameKey]?.tokenize(' ')?.init()?.join(' '))
                .lastname(input[type.lastnameKey]  ?: input[type.nameKey]?.tokenize(' ')?.last())
                .website(input.WEBSEITE)
                .company(input[type.companyKey])
//                .email(input.)
                .twitter(input.LINKTWITTER)
//                .gplus(input.)
                .facebook(input.LINKFACEBOOK)
                .xing(input.LINKXING)
                .linkedin(input.LINKEDIN)
                .bio(input.PROFILTEXT)
                .photoId(md5(input.PROFILFOTO))
                .build() : null
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
