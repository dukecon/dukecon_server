package org.dukecon.server.business

import org.dukecon.model.Audience
import org.dukecon.model.Conference
import org.dukecon.model.Language
import org.dukecon.model.MetaData
import org.dukecon.model.Room
import org.dukecon.model.TalkType
import org.dukecon.model.Track

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class MetaDataExtractor {
    def talksJson
    String conferenceUrl = 'http://dukecon.org'
    String conferenceName = 'DukeCon Conference'

    private static Language en = Language.builder().code("en").name("English").build()
    private static Language de = Language.builder().code("de").name("Deutsch").build()

    MetaData buildMetaData() {
        MetaData.builder().conference(this.conference).rooms(this.rooms).tracks(this.tracks).languages(this.languages).defaultLanguage(this.defaultLanguage).audiences(this.audiences).talkTypes(this.talkTypes).build()
    }

    Conference getConference() {
        Conference conference = Conference.builder()
                .id(talksJson.ID_KONGRESS.unique().first())
                .name(conferenceName)
                .url(conferenceUrl)
                .build();
    }


    List<Track> getTracks() {
        return talksJson.findAll{it.TRACK}.collect {[it.ORDERT, it.TRACK, it.TRACK_EN]}.unique().sort {it.first()}.collect {
            Track.builder().order(it.first()).names([de:it[1], en:it[2]]).build()
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
        return talksJson.findAll{it.AUDIENCE}.collect {[it.AUDIENCE, it.AUDIENCE_EN]}.unique().sort {it.first()}.collect {
            Audience.builder().order(i++).names([de:it[0], en:it[1]]).build()
        }
    }

    List<Room> getRooms() {
        return talksJson.findAll{it.RAUMNAME}.collect {[it.RAUM_NR, it.RAUMNAME]}.unique().sort {it.first()}.collect {
            Room.builder().order(it.first()?.toInteger()).name(it.last()).build()
        }
    }

    List<TalkType> getTalkTypes() {
        int i = 1
        return talksJson.findAll{it.VORTRAGSTYP}.collect {[it.VORTRAGSTYP, it.VORTRAGSTYP_EN]}.unique().sort {it.first()}.collect {
            TalkType.builder().names(de:it[0], en:it[1]).order(i++).build()
        }
    }
}
