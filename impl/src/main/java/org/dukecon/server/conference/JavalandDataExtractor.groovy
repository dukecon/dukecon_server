package org.dukecon.server.conference

import groovy.util.logging.Slf4j
import org.dukecon.model.*

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        }.withIndex().collect {track, index ->
            Track.builder().id(index + 1 as String).order(track.first()).names([de: track[1], en: track[2]]).icon("track_${track.first()}.png").build()
        }
    }

    List<Language> getLanguages() {
        int i = 1
        return talksJson.findAll { it.SPRACHE }.collect { [it.SPRACHE, it.SPRACHE_EN] }.unique().sort {
            it.first()
        }.collect {
            def lang = toIsoLanguageCode(it[1])
            Language.builder().id(lang).order(i++).names([de: it[0], en: it[1]]).icon("language_${lang}.png").build()
        }
    }

    private String toIsoLanguageCode(String language) {
        def iso;
        try {
            iso = Locale."${language.toUpperCase()}".language
        } catch (MissingPropertyException) {
            // Usually: "German & English
            iso = language.toLowerCase().replaceAll("[^a-z]", "");
            log.warn("unknown language {}", language);
        }
        return iso;
    }

    Language getDefaultLanguage() {
        return languages.first()
    }

    Language getLanguage(String name) {
        return name ? languages.find { toIsoLanguageCode(name) == it.id } : null
    }

    List<Audience> getAudiences() {
        return talksJson.findAll { it.AUDIENCE }.collect { [it.AUDIENCE, it.AUDIENCE_EN] }.unique().sort {
            it.first()
        }.withIndex().collect {audience, index ->
            Audience.builder().id(index + 1 as String).icon("audience_${index + 1}.png").order(index + 1).names([de: audience[0], en: audience[1]]).build()
        }
    }

    List<Location> getLocations() {
        return talksJson.findAll { it.RAUMNAME }.collect { [it.RAUM_NR, it.RAUMNAME] }.unique().sort {
            it.first()
        }.withIndex().collect {room, index ->
            Location.builder().id(index + 1 as String).order(room.first()?.toInteger()).names(de: room[1], en: room[1]).icon("location_${room.first()}.png").build()
        }
    }

    List<EventType> getEventTypes() {
        return talksJson.findAll { it.VORTRAGSTYP }.collect { [it.VORTRAGSTYP, it.VORTRAGSTYP_EN] }.unique().sort {
            it.first()
        }.withIndex().collect {type, index ->
            EventType.builder().id(index + 1 as String).icon("eventType_${index + 1}.png").order(index + 1).names(de: type[0], en: type[1]).build()
        }
    }

    private List<Speaker> getSpeakers() {
        def result = talksJson.findAll { it.ID_PERSON }.collect { t ->
            Speaker.builder().id(t.ID_PERSON?.toString()).name(t.REFERENT_NAME).company(t.REFERENT_FIRMA).build()
        } + talksJson.findAll { it.ID_PERSON_COREF }.collect { t ->
            Speaker.builder().id(t.ID_PERSON_COREF?.toString()).name(t.COREFERENT_NAME).company(t.COREFERENT_FIRMA).build()
        } + talksJson.findAll { it.ID_PERSON_COCOREF }.collect { t ->
            Speaker.builder().id(t.ID_PERSON_COCOREF?.toString()).name(t.COCOREFERENT_NAME).company(t.COCOREFERENT_FIRMA).build()
        }
        result.flatten().unique { it.id }
    }

    /**
     * @param talkLookup
     * @return list of speakers with their events assigned
     */
    private List<Speaker> getSpeakersWithEvents(Map<String, List<Event>> talkLookup = getSpeakerIdToEvents()) {
        speakers.collect { Speaker s ->
            s.events = ([] + talkLookup[s.id]).flatten()
            s
        }
    }

    /**
     * @return map with speaker ids as key and a list all events of this speaker as value
     */
    private Map<String, List<Event>> getSpeakerIdToEvents() {
        (events.findAll { it.speakers }.collect { [it.speakers.first().id, it] } + events.collect {
            [it.speakers[1]?.id, it]
        })
                .inject([:]) { map, list ->
            if (!map[list.first()]) {
                map[list.first()] = []
            }
            map[list.first()] << list[1]
            return map
        }.findAll { k, v -> k }
    }

    private List<Event> getEvents(Map<String, Speaker> speakerLookup = speakers.collectEntries { [it.id, it] }) {
        return talksJson.collect { eventJson ->
            return Event.builder()
                    .id(eventJson.ID.toString())
                    .start(LocalDateTime.parse(eventJson.DATUM_ES_EN + ' ' + eventJson.BEGINN, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .end(LocalDateTime.parse(eventJson.DATUM_ES_EN + ' ' + eventJson.ENDE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .title(eventJson.TITEL)
                    .abstractText(eventJson.ABSTRACT_TEXT?.replaceAll("&quot;", "\""))
                    .language(getLanguage(eventJson.SPRACHE_EN))
                    .demo(eventJson.DEMO != null && eventJson.DEMO.equalsIgnoreCase('ja'))
                    .track(tracks.find { eventJson.TRACK_EN == it.names.en })
                    .audience(audiences.find { eventJson.AUDIENCE_EN == it.names.en })
                    .type(eventTypes.find { eventJson.VORTRAGSTYP_EN == it.names.en })
                    .location(locations.find { eventJson.RAUMNAME == it.names.en })
                    .speakers([speakerLookup[eventJson.ID_PERSON?.toString()], speakerLookup[eventJson.ID_PERSON_COREF?.toString()], speakerLookup[eventJson.ID_PERSON_COCOREF?.toString()]].findAll {
                it
            })
                    .build()
        }
    }
}
