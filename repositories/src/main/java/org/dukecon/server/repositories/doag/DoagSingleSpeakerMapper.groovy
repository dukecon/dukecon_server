package org.dukecon.server.repositories.doag

import groovy.util.logging.Slf4j
import org.dukecon.model.Speaker

import java.security.MessageDigest

/**
 * Maps a single speaker from json input map to @{@link Speaker}.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Slf4j
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

    private static String lastNameFromName(String name) {
        if (name) {
            List tokens = name.tokenize(' ')
            if (tokens.size() > 0) {
                return tokens.last()
            }
        }
        return name
    }

    private static List extractNameParts(String first, String last, String name) {
        if (!first && !last && !name) return []
        String lastName = last ?: lastNameFromName(name)
        String firstName = first ?: (name - lastName).trim()
        return [firstName, lastName, name ?: "${firstName} ${lastName}"]
    }

    DoagSingleSpeakerMapper(input, Type type = Type.DEFAULT) {
        def (firstName, lastName, fullName) = extractNameParts(input[type.firstnameKey], input[type.lastnameKey], input[type.nameKey])
        if (lastName) {
            this.speaker = input[type.idKey] ? Speaker.builder()
                    .id(input[type.idKey]?.toString())
                    .name(fullName)
                    .firstname(firstName)
                    .lastname(lastName)
                    .website(input.WEBSEITE)
                    .company(input[type.companyKey])
                    .twitter(input.LINKTWITTER)
                    .facebook(input.LINKFACEBOOK)
                    .xing(input.LINKXING)
                    .linkedin(input.LINKEDIN)
                    .bio(input.PROFILTEXT)
                    .photoId(md5(input.PROFILFOTO))
                    .build() : null
        } else {
            this.speaker = null
        }
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
