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
                .events(this.events)
                .build()
        conf.metaData.conference = conf
        conf.speakers = getSpeakersWithEvents()
        return conf
    }

    private MetaData getMetaData() {
        MetaData.builder().locations(this.locations).tracks(this.tracks).languages(this.languages).defaultLanguage(this.defaultLanguage).audiences(this.audiences).eventTypes(this.eventTypes).defaultIcon("Unknown.png").build()
    }

    List<Track> getTracks() {
        return talksJson.findAll { it.TRACK }.collect { [it.ORDERT, it.TRACK, it.TRACK_EN] }.unique().sort {
            it.first()
        }.collect {
            Track.builder().id(it.first()?.toString()).order(it.first()).names([de: it[1], en: it[2]]).icon("track_${it.first()}.png").build()
        }
    }

    List<Language> getLanguages() {
        int i = 1
        return talksJson.findAll { it.SPRACHE }.collect { [it.SPRACHE, it.SPRACHE_EN] }.unique().sort {
            it.first()
        }.collect {
            def lang = Locale."${it[1].toUpperCase()}".language
            Language.builder().id(lang).order(i++).names([de: it[0], en: it[1]]).icon("language_${lang}.png").build()
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
            Audience.builder().id(i.toString()).icon("audience_${i}.png").order(i++).names([de: it[0], en: it[1]]).build()
        }
    }

    List<Location> getLocations() {
        return talksJson.findAll { it.RAUMNAME }.collect { [it.RAUM_NR, it.RAUMNAME] }.unique().sort {
            it.first()
        }.collect {
            Location.builder().id(it.first()?.toString()).order(it.first()?.toInteger()).names(de: it[1], en: it[1]).icon("location_${it.first()}.png").build()
        }
    }

    List<EventType> getEventTypes() {
        int i = 1
        return talksJson.findAll { it.VORTRAGSTYP }.collect { [it.VORTRAGSTYP, it.VORTRAGSTYP_EN] }.unique().sort {
            it.first()
        }.collect {
            EventType.builder().id(i.toString()).icon("eventType_${i}.png").order(i++).names(de: it[0], en: it[1]).build()
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
     * @return list of speakers with their events assigned
     */
    private List<Speaker> getSpeakersWithEvents(Map<String, List<Event>> talkLookup = getSpeakerIdToEvents()) {
        speakers.collect {Speaker s ->
            s.events = ([] + talkLookup[s.id]).flatten()
            s
        }
    }

    /**
     * @return map with speaker ids as key and a list all events of this speaker as value
     */
    private Map<String, List<Event>> getSpeakerIdToEvents() {
        (events.findAll{it.speakers}.collect{[it.speakers.first().id, it]} + events.collect{[it.speakers[1]?.id, it]})
                .inject([:]){map, list ->
                    if(!map[list.first()]) {
                        map[list.first()] = []
                    }
                    map[list.first()] << list[1]
                    return map
                }.findAll {k,v -> k}
    }

    private List<Event> getEvents(Map<String, Speaker> speakerLookup = speakers.collectEntries{[it.id, it]}) {
        return talksJson.collect { t ->
            return Event.builder()
                    .id(t.ID.toString())
                    .start(t.DATUM_ES_EN + 'T' + t.BEGINN)
                    .end(t.DATUM_ES_EN + 'T' + t.ENDE)
                    .title(t.TITEL)
                    .abstractText(t.ABSTRACT_TEXT?.replaceAll("&quot;", "\""))
                    .language(getLanguage(t.SPRACHE_EN))
                    .demo(t.DEMO != null && t.DEMO.equalsIgnoreCase('ja'))
                    .track(tracks.find{t.ORDERT == it.order})
                    .audience(audiences.find {t.AUDIENCE_EN == it.names.en})
                    .type(eventTypes.find {t.VORTRAGSTYP_EN == it.names.en})
                    .location(locations.find {t.RAUM_NR == it.id})
                    .speakers([speakerLookup[t.ID_PERSON?.toString()], speakerLookup[t.ID_PERSON_COREF?.toString()], speakerLookup[t.ID_PERSON_COCOREF?.toString()]].findAll {it})
                    .build()
        }
    }
}
