package org.dukecon.server.repositories.sched

import groovy.util.logging.Slf4j
import org.dukecon.model.*
import org.dukecon.server.repositories.ConferenceDataExtractor
import org.dukecon.server.repositories.RawDataMapper

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Extracts conferences information, talks and speakers for Sched conferences (e. g. ApacheCon).
 *
 * TODO must be refactored like DoagDataExtractor
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 * @author Christofer Dutz, christofer.dutz@codecentric.de, @ChristoferDutz
 */
@Slf4j
class SchedDataExtractor implements ConferenceDataExtractor {

    def conferenceId
    private LocalDate startDate
    private String conferenceName
    private String conferenceUrl

    def conferenceJson
    def eventsJson

    SchedDataExtractor() {
    }

    SchedDataExtractor(String conferenceId, rawDataResource, LocalDate startDate, String conferenceName = 'DukeCon Conference', String conferenceUrl = 'http://dukecon.org') {
        this.conferenceId = conferenceId
        this.startDate = startDate
        this.conferenceName = conferenceName
        this.conferenceUrl = conferenceUrl
    }

    @Override
    Conference getConference() {
        Conference conf = Conference.builder()
                .id(conferenceId.toString())
                .name(conferenceJson.title.toString())
                .url(conferenceJson.base.toString())
                .metaData(metaData)
                .speakers(speakers)
                .events(events)
                .build()
//        conf.speakers = getSpeakersWithEvents()
        return conf
    }

    @Override
    RawDataMapper getRawDataMapper() {
        return null
    }

    private MetaData getMetaData() {
        MetaData.builder().locations(locations).tracks(tracks).languages(languages).defaultLanguage(defaultLanguage)
                .audiences(audiences).eventTypes(eventTypes).defaultIcon("Unknown.png").build()
    }

    List<Track> getTracks() {
        return conferenceJson.type.collect {
            String eventTypeId, eventType ->
                return Track.builder().id(eventTypeId).names(en: eventType.name.toString()).order(
                        Integer.valueOf(eventType.sortorder.toString())).icon(
                        "event_type_${eventTypeId.replaceAll("[^A-Za-z0-9 ]", "_")}.png").build()
        }
    }

    List<Language> getLanguages() {
        Language en = Language.builder().id(conferenceJson.locale.toString()).names(en: "English").order(1).icon(
                "language_${conferenceJson.locale.replaceAll("[^A-Za-z0-9 ]", "_")}.png").build();
        return Arrays.asList(en)
    }

    private String toIsoLanguageCode(String language) {
        def iso
        try {
            iso = Locale."${language.toUpperCase()}".language
        } catch (MissingPropertyException e) {
            // Usually: "German & English
            iso = language.toLowerCase().replaceAll("[^a-z]", "")
            log.trace("unknown language '{}'", language)
        }
        return iso
    }

    Language getDefaultLanguage() {
        return languages.first()
    }

    List<Audience> getAudiences() {
        return null
    }

    List<Location> getLocations() {
        return conferenceJson.venues.collect { String room, String index ->
            return Location.builder().id(index).names(en: room).icon(
                    "location_${index.replaceAll("[^A-Za-z0-9 ]", "_")}.png").build()
        }
    }

    List<EventType> getEventTypes() {
        return null
    }

    private List<Speaker> getSpeakers() {
        return eventsJson.findAll {
            return it.speakers
        }.collect {
            talk ->
                // TODO: set first- and lastname
                return Speaker.builder().id(talk.speakers.toString()).name(talk.speakers.toString()).build()
        }.flatten().unique {
            return it.id
        }
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
        return eventsJson.collect { eventJson ->
            return Event.builder()
                    .id(eventJson.id.toString())
                    .start(LocalDateTime.parse(
                        eventJson.event_start,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .end(LocalDateTime.parse(
                        eventJson.event_end,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .title(eventJson.name.toString())
                    .abstractText(eventJson.description?.toString()?.replaceAll("&quot;", "\"")?.replaceAll("\r\n", "\n"))
                    .language(defaultLanguage)
                    .demo(false)
                    .track(tracks.find { eventJson.event_type == it.names.en })
                    .location(locations.find { eventJson.venue == it.names.en })
                    .speakers([speakerLookup[eventJson.speakers?.toString()]].findAll {
                it
            }).build()
        }.findAll {Event event -> event.start.until(event.end, ChronoUnit.MINUTES) > 0}
    }

}
