package org.dukecon.server.business

import groovy.util.logging.Slf4j
import org.dukecon.model.*

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Slf4j
class JavalandDataExtractor {
    def talksJson
    String conferenceUrl = 'http://dukecon.org'
    String conferenceName = 'DukeCon Conference'

    Conference buildConference() {
        Conference conf = Conference.builder()
                .id(talksJson.ID_KONGRESS.unique().first()?.toString())
                .name(conferenceName)
                .url(conferenceUrl)
                .rooms(this.rooms)
                .tracks(this.tracks)
                .talks(this.talks)
                .metaData(metaData)
                .build()
//        conf.metaData.conference = conf
        return conf
    }

    private MetaData getMetaData() {
        MetaData.builder().languages(this.languages).defaultLanguage(this.defaultLanguage).audiences(this.audiences).talkTypes(this.talkTypes).build()
    }

    List<Track> getTracks() {
        return talksJson.findAll { it.TRACK }.collect { [it.ORDERT, it.TRACK, it.TRACK_EN] }.unique().sort {
            it.first()
        }.collect {
            Track.builder().id(it.first()?.toString()).order(it.first()).names([de: it[1], en: it[2]]).build()
        }
    }

    List<Language> getLanguages() {
        int i = 1
        return talksJson.findAll { it.SPRACHE }.collect { [it.SPRACHE, it.SPRACHE_EN] }.unique().sort {
            it.first()
        }.collect {
            def lang = Locale."${it[1].toUpperCase()}".language
            Language.builder().id(lang).order(i++).names([de: it[0], en: it[1]]).build()
        }
    }

    Language getDefaultLanguage() {
        return languages.first()
    }

    Language getLanguage(String name) {
        return name ? languages.find {Locale."${name.toUpperCase()}".language == it.id} : null
    }

    List<Audience> getAudiences() {
        int i = 1
        return talksJson.findAll { it.AUDIENCE }.collect { [it.AUDIENCE, it.AUDIENCE_EN] }.unique().sort {
            it.first()
        }.collect {
            Audience.builder().id(i.toString()).order(i++).names([de: it[0], en: it[1]]).build()
        }
    }

    List<Room> getRooms() {
        return talksJson.findAll { it.RAUMNAME }.collect { [it.RAUM_NR, it.RAUMNAME] }.unique().sort {
            it.first()
        }.collect {
            Room.builder().id(it.first()?.toString()).order(it.first()?.toInteger()).name(it.last()).build()
        }
    }

    List<TalkType> getTalkTypes() {
        int i = 1
        return talksJson.findAll { it.VORTRAGSTYP }.collect { [it.VORTRAGSTYP, it.VORTRAGSTYP_EN] }.unique().sort {
            it.first()
        }.collect {
            TalkType.builder().names(de: it[0], en: it[1]).order(i++).build()
        }
    }

    private List<Talk> getTalks(boolean v2 = false) {
        Set<String> talkIds = new HashSet<>()
        return talksJson.collect { t ->
            String id = t.ID.toString()
            if (talkIds.contains(id)) {
                log.error("Duplicate Talk ID '{}' in raw data!", id)
                return
            }
            Speaker speaker = Speaker.builder().name(t.REFERENT_NAME).company(t.REFERENT_FIRMA).defaultSpeaker(true).build()
            Speaker speaker2 = t.COREFERENT_NAME == null ? null : Speaker.builder().name(t.COREFERENT_NAME).company(t.COREFERENT_FIRMA).build()
            List<Speaker> speakers = [speaker]
            if (speaker2) {
                speakers.add(speaker2)
            }
            def builder = Talk.builder()
                    .id(id)
                    .start(t.DATUM_ES_EN + 'T' + t.BEGINN)
                    .end(t.DATUM_ES_EN + 'T' + t.ENDE)
                    .title(t.TITEL)
                    .abstractText(t.ABSTRACT_TEXT?.replaceAll("&quot;", "\""))
                    .language(getLanguage(t.SPRACHE_EN))
                    .demo(t.DEMO != null && t.DEMO.equalsIgnoreCase('ja'))
                    .speakers(speakers)
                    .track(tracks.find{t.ORDERT == it.id})
                    .audience(audiences.find {t.AUDIENCE_EN == it.names.en})
                    .type(talkTypes.find {t.VORTRAGSTYP_EN == it.names.en})
                    .room(rooms.find {t.RAUM_NR == it.id})
            if (v2) {
                builder.trackNumber(metaDataLookup.tracks[t.TRACK])
                        .roomNumber(metaDataLookup.rooms[t.RAUMNAME])
                        .levelNumber(metaDataLookup.audiences[t.AUDIENCE])
                        .typecNumber(metaDataLookup.talkTypes[t.VORTRAGSTYP])
            }
            return builder.build()
        }
    }

}
