package org.dukecon.server.business

import org.dukecon.model.Audience
import org.dukecon.model.Language
import org.dukecon.model.Room
import org.dukecon.model.Track

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class MetaDataExtractor {
    def talksJson

    private static Language en = Language.builder().code("en").name("English").build()
    private static Language de = Language.builder().code("de").name("Deutsch").build()

    List<Track> getTracks() {
        int i = 1
        return talksJson.collect {[it.ORDERT, it.TRACK, it.TRACK_EN]}.unique().sort {it.first()}.collect {
            Track.builder().code(it.first()).order(i++).names([de:it[1], en:it[2]]).build()
        }
    }

    List<Language> getLanguages() {
        return [de, en]
    }

    Language getDefaultLanguage() {
        return de
    }

    List<Audience> getAudiences() {
        int i = 1
        return talksJson.collect {[it.AUDIENCE, it.AUDIENCE_EN]}.unique().sort {it.first()}.collect {
            Audience.builder().order(i++).names([de:it[0], en:it[1]]).build()
        }
    }

    List<Room> getRooms() {
        return talksJson.collect {[it.RAUM_NR, it.RAUMNAME]}.unique().sort {it.first()}.collect {
            Room.builder().number(it.first()?.toInteger()).name(it.last()).build()
        }
    }
}
