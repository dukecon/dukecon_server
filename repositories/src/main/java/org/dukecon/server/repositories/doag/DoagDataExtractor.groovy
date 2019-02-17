package org.dukecon.server.repositories.doag

import groovy.util.logging.Slf4j
import org.dukecon.model.*
import org.dukecon.server.conference.ConferencesConfiguration
import org.dukecon.server.conference.SpeakerImageService
import org.dukecon.server.favorites.PreferencesService
import org.dukecon.server.repositories.ConferenceDataExtractor
import org.dukecon.server.repositories.RawDataMapper
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import static com.xlson.groovycsv.CsvParser.parseCsv

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Slf4j
class DoagDataExtractor implements ConferenceDataExtractor, ApplicationContextAware {

    private SpeakerImageService speakerImageService
    private PreferencesService preferencesService

    private final RawDataMapper rawDataMapper
    def talksJson
    def speakersJson
    def additionalDataJson
    private final String conferenceId
    private final Class<? extends ConferenceDataExtractor> extractorClass
    private final LocalDate startDate
    private final String conferenceUrl = 'http://dukecon.org'
    private final String conferenceHomeUrl = 'http://javaland.eu'
    private final String conferenceName = 'DukeCon Conference'

//    static DoagDataExtractor fromFile(String filename, ConferencesConfiguration.Conference config) {
//        new DoagDataExtractor(config, new DoagJsonMapper(new RawDataResources(filename)), new SpeakerImageService())
//    }

    DoagDataExtractor(ConferencesConfiguration.Conference config, RawDataMapper rawDataMapper, SpeakerImageService speakerImageService) {
        log.debug ("Extracting data for '{}'", config)
        this.conferenceId = config.id
        this.rawDataMapper = rawDataMapper
        this.startDate = config.startDate
        this.conferenceName = config.name
        this.conferenceUrl = config.url
        this.conferenceHomeUrl = config.homeUrl
        this.speakerImageService = speakerImageService
    }

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.preferencesService = applicationContext.getBean(PreferencesService)
    }

    @Override
    Conference getConference() {
        return buildConference()
    }

    @Override
    RawDataMapper getRawDataMapper() {
        return this.rawDataMapper
    }

    Conference buildConference() {
        log.debug("Building conference '{}' (name: {}, url: {})", conferenceId, conferenceName, conferenceUrl)
        this.rawDataMapper.initMapper()
        this.talksJson = this.rawDataMapper.asMap().eventsData
        this.speakersJson = this.rawDataMapper.asMap().speakersData
        this.additionalDataJson = this.rawDataMapper.asMap().additionalData
        DoagSpeakersMapper mapper = DoagSpeakersMapper.createFrom(talksJson, speakersJson, parseTwitterHandles())
        mapper.photos.each {
            speakerImageService.addImage(it)
        }
        def events = this.getEvents()
        Conference conf = Conference.builder()
                .id(conferenceId)
                .name(conferenceName)
                .url(conferenceUrl)
                .homeUrl(conferenceHomeUrl)
                .metaData(metaData)
                .speakers(mapper.speakers.values() as List)
                .events(events)
                .build()
        conf.speakers = getSpeakersWithEvents(conf.speakers)
        return conf
    }

    private static Map<String, String> parseTwitterHandles() {
        Map<String, String> result = [:]
        InputStream twitterData = new org.springframework.core.io.ClassPathResource("twitterhandles.csv").inputStream
        def csv = parseCsv(new InputStreamReader(twitterData))
        csv.each { line ->
            result.put(line.Speaker.trim(), line.TwitterHandle?.trim())
        }
        return result
    }

    private MetaData getMetaData() {
        MetaData.builder().locations(this.locations).tracks(this.tracks).languages(this.languages).defaultLanguage(this.defaultLanguage).audiences(this.audiences).eventTypes(this.eventTypes).defaultIcon("Unknown.png").build()
    }

    List<Track> getTracks() {
        return talksJson.findAll { it.TRACK }.collect { [it.ORDERT, it.TRACK, it.TRACK_EN] }.unique().sort {
            it.first()
        }.withIndex().collect { track, index ->
            Track.builder().id(index + 1 as String).order(Integer.valueOf(track[0])).names([de: track[1], en: track[2]]).icon("track_${track[0]}.png").build()
        }
    }

    List<Language> getLanguages() {
        int i = 1
        return talksJson.findAll { it.SPRACHE }.collect { [it.SPRACHE, it.SPRACHE_EN] }.unique().sort {
            it.first()
        }.collect {
            def lang = toIsoLanguageCode(it[1])
            Language.builder().id(Integer.toString(i)).code(lang).order(i++).names([de: it[0], en: it[1]]).icon("language_${lang}.png").build()
        }
    }

    private String toIsoLanguageCode(String language) {
        def iso;
        try {
            iso = Locale."${language.toUpperCase()}".language
        } catch (MissingPropertyException) {
            // Usually: "German & English
            iso = language.toLowerCase().replaceAll("[^a-z]", "");
            log.trace("unknown language {}", language);
        }
        return iso;
    }

    Language getDefaultLanguage() {
        return languages.isEmpty() ? null : languages.first()
    }

    Language getLanguage(String name) {
        return name ? languages.find { toIsoLanguageCode(name) == it.code } : null
    }

    List<Audience> getAudiences() {
        return talksJson.findAll { it.AUDIENCE }.collect { [it.AUDIENCE, it.AUDIENCE_EN] }.unique().sort {
            it.first()
        }.withIndex().collect { audience, index ->
            Audience.builder().id(index + 1 as String).icon("audience_${index + 1}.png").order(index + 1).names([de: audience[0], en: audience[1]]).build()
        }
    }

    List<Location> getLocations() {
        return talksJson.findAll {
            it.RAUMNAME }.collect {
                [it.RAUM_NR,
                 it.RAUMNAME,
                 parseInt(it.SITZPLATZ)
                ]
            }.unique().sort {
                    it.first()
            }.withIndex().collect { room, index ->
                Location.builder().id(index + 1 as String).order(room.first()?.toInteger()).capacity(room[2]).names(de: room[1], en: room[1]).icon("location_${room.first()}.png").build()
            }
    }

    private static int parseInt(String value) {
        def number = value?.replace('.', '') ?: ""
        number.isInteger() ? number.toInteger() : 0
    }

    List<EventType> getEventTypes() {
        return talksJson.findAll { it.VORTRAGSTYP }.collect { [it.VORTRAGSTYP, it.VORTRAGSTYP_EN] }.unique().sort {
            it.first()
        }.withIndex().collect { type, index ->
            EventType.builder().id(index + 1 as String).icon("eventType_${index + 1}.png").order(index + 1).names(de: type[0], en: type[1]).build()
        }
    }

    @Deprecated
    private List<Speaker> getSpeakers() {
        DoagSpeakersMapper mapper = DoagSpeakersMapper.createFrom(talksJson, speakersJson)
        mapper.photos.each {
            speakerImageService.addImage(it)
        }
        return mapper.speakers.values() as List
    }

    /**
     * @param talkLookup
     * @return list of speakers with their events assigned
     */
    private List<Speaker> getSpeakersWithEvents(List<Speaker> speakers, Map<String, List<Event>> talkLookup = getSpeakerIdToEvents()) {
        speakers.collect { Speaker s ->
            s.events = ([] + talkLookup[s.id]).flatten()
            log.trace("Speaker '{}' has #{} events", s.name, s.events.size())
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

    @Deprecated
    private List<Event> getEvents(Map<String, Speaker> speakerLookup = speakers.collectEntries { [it.id, it] }) {
        Map<String, Integer> favoritesPerEvent = preferencesService?.allEventFavorites ?: [:]
        Map<Integer, Map<String, String>> additionalDataPerEvent = additionalDataJson?.collectEntries {[it.SEMINAR_ID, it]} ?: [:]
        List<Event> events = talksJson.collect {eventJson ->
            getEvent(eventJson, speakerLookup, favoritesPerEvent, additionalDataPerEvent)
        }
        List<Event> result = events.findAll {
            Event event ->
            if (event.start.isAfter(event.end)) {
                log.warn("Event '{}' will be ignored, because start time ({}) is behind end time ({})", event.title, event.start, event.end)
            }
            return event.start.until(event.end, ChronoUnit.MINUTES) > 0
        }
        return result
    }

    private Event getEvent(eventJson, Map<String, Speaker> speakerLookup, Map<String, Integer> favoritesPerEvent, Map<Integer, Map<String, String>> additionalDataPerEvent) {
        Event.builder()
                .id(eventJson.ID.toString())
        // TODO: parse TIMESTAMP and TIMESTAMP_ENDE
                .start(LocalDateTime.parse(eventJson.DATUM_ES_EN + ' ' + eventJson.BEGINN, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .end(LocalDateTime.parse(eventJson.DATUM_ES_EN + ' ' + eventJson.ENDE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .title(eventJson.TITEL)
                .abstractText(eventJson.ABSTRACT_TEXT?.replaceAll("&quot;", "\"")?.replaceAll("\r\n", "\n"))
                .language(getLanguage(eventJson.SPRACHE_EN))
                .simultan(eventJson?.SIMULTAN == '1')
                .demo(eventJson.DEMO != null && eventJson.DEMO.equalsIgnoreCase('ja'))
                .track(tracks.find { eventJson.TRACK_EN == it.names.en })
                .audience(audiences.find { eventJson.AUDIENCE_EN == it.names.en })
                .type(eventTypes.find { eventJson.VORTRAGSTYP_EN == it.names.en })
                .location(locations.find { eventJson.RAUMNAME == it.names.en })
                .veryPopular(eventJson.AUSGEBUCHT as boolean)
                .fullyBooked(false)
                .numberOfFavorites((favoritesPerEvent[eventJson.ID.toString()] ?: 0) as int)
                .keywords([de: eventJson.KEYWORDS?.tokenize(',') ?: [], en: eventJson.KEYWORDS_EN?.tokenize(',') ?: []])
                .documents([slides: additionalDataPerEvent[eventJson.ID]?.PRESENTATION, manuscript: additionalDataPerEvent[eventJson.ID]?.MANUSCRIPT, other: additionalDataPerEvent[eventJson.ID]?.OTHER])
                .speakers([speakerLookup[eventJson.ID_PERSON?.toString()], speakerLookup[eventJson.ID_PERSON_COREF?.toString()], speakerLookup[eventJson.ID_PERSON_COCOREF?.toString()]].findAll {
                    it
                })
                .build()
    }
}
