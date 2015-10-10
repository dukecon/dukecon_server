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
                .metaData(metaData)
                .speakers(this.speakers)
                .talks(this.talks)
                .build()
        conf.metaData.conference = conf
        conf.speakers = getSpeakersWithTalks()
        return conf
    }

    private MetaData getMetaData() {
        MetaData.builder().rooms(this.rooms).tracks(this.tracks).languages(this.languages).defaultLanguage(this.defaultLanguage).audiences(this.audiences).talkTypes(this.talkTypes).build()
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
            Room.builder().id(it.first()?.toString()).order(it.first()?.toInteger()).names(de: it[1], en: it[1]).build()
        }
    }

    List<TalkType> getTalkTypes() {
        int i = 1
        return talksJson.findAll { it.VORTRAGSTYP }.collect { [it.VORTRAGSTYP, it.VORTRAGSTYP_EN] }.unique().sort {
            it.first()
        }.collect {
            TalkType.builder().id(i.toString()).order(i++).names(de: it[0], en: it[1]).build()
        }
    }

    private List<Speaker> getSpeakers() {
        def result = talksJson.findAll {it.ID_PERSON}.collect {t ->
            Speaker.builder().id(t.ID_PERSON?.toString()).name(t.REFERENT_NAME).company(t.REFERENT_FIRMA).build()
        } + talksJson.findAll {it.ID_PERSON_COREF}.collect {t ->
            Speaker.builder().id(t.ID_PERSON_COREF?.toString()).name(t.COREFERENT_NAME).company(t.COREFERENT_FIRMA).build()
        } + talksJson.findAll {it.ID_PERSON_COCOREF}.collect {t ->
            Speaker.builder().id(t.ID_PERSON_COCOREF?.toString()).name(t.COCOREFERENT_NAME).company(t.COCOREFERENT_FIRMA).build()
        }
        result.flatten().unique{it.id}
    }

    /**
     * @param talkLookup
     * @return list of speakers with their talks assigned
     */
    private List<Speaker> getSpeakersWithTalks(Map<String, List<Talk>> talkLookup = getSpeakerIdToTalks()) {
        speakers.collect {Speaker s ->
            s.talks = ([] + talkLookup[s.id]).flatten()
            s
        }
    }

    /**
     * @return map with speaker ids as key and a list all talks of this speaker as value
     */
    private Map<String, List<Talk>> getSpeakerIdToTalks() {
        (talks.findAll{it.speakers}.collect{[it.speakers.first().id, it]} + talks.collect{[it.speakers[1]?.id, it]})
                .inject([:]){map, list ->
                    if(!map[list.first()]) {
                        map[list.first()] = []
                    }
                    map[list.first()] << list[1]
                    return map
                }.findAll {k,v -> k}
    }

    private List<Talk> getTalks(Map<String, Speaker> speakerLookup = speakers.collectEntries{[it.id, it]}) {
        return talksJson.collect { t ->
            return Talk.builder()
                    .id(t.ID.toString())
                    .start(t.DATUM_ES_EN + 'T' + t.BEGINN)
                    .end(t.DATUM_ES_EN + 'T' + t.ENDE)
                    .title(t.TITEL)
                    .abstractText(t.ABSTRACT_TEXT?.replaceAll("&quot;", "\""))
                    .language(getLanguage(t.SPRACHE_EN))
                    .demo(t.DEMO != null && t.DEMO.equalsIgnoreCase('ja'))
                    .track(tracks.find{t.ORDERT == it.order})
                    .audience(audiences.find {t.AUDIENCE_EN == it.names.en})
                    .type(talkTypes.find {t.VORTRAGSTYP_EN == it.names.en})
                    .room(rooms.find {t.RAUM_NR == it.id})
                    .speakers([speakerLookup[t.ID_PERSON?.toString()], speakerLookup[t.ID_PERSON_COREF?.toString()], speakerLookup[t.ID_PERSON_COCOREF?.toString()]].findAll {it})
                    .build()
        }
    }
}
