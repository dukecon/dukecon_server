package org.dukecon.server.repositories.apache

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
import java.time.ZoneId

/**
 * @author Christofer Dutz, christofer.dutz@codecentric.de, @ChristoferDutz
 */
@Slf4j
class ApacheDataExtractor implements ConferenceDataExtractor, ApplicationContextAware {

    private SpeakerImageService speakerImageService
    private PreferencesService preferencesService

    private final RawDataMapper rawDataMapper
    def conferenceJson
    private final String conferenceId
    private final LocalDate startDate
    private final String conferenceUrl
    private final String conferenceHomeUrl
    private final String conferenceName

    ApacheDataExtractor(ConferencesConfiguration.Conference config, RawDataMapper rawDataMapper, SpeakerImageService speakerImageService) {
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
        this.conferenceJson = this.rawDataMapper.asMap().eventsData.rooms
        ParseContext ctx = new ParseContext()
        ctx.languages.put("EN", new Language("EN", "EN", 1, [EN: "English"], null))
        parseRooms(ctx, conferenceJson)
        Conference conference = Conference.builder().id(conferenceId)
                .name(conferenceName)
                .url(conferenceUrl)
                .homeUrl(conferenceHomeUrl)
                .events(new ArrayList<Event>(ctx.events.values()))
                .speakers(new ArrayList<Speaker>(ctx.speakers.values()))
                .build()
        return conference
    }

    private static void parseRooms(ParseContext ctx, def json) {
        List<Map> rooms = (List<Map>) json
        for (Map room : rooms) {
            String roomName = room.get("name")
            List<Map> days = (List<Map>) room.get("days")
            for(Map day : days) {
                parseDay(ctx, roomName, day)
            }
        }
    }

    private static void parseDay(ParseContext ctx, String roomName, def json) {
        List<Map> slots = json.get("slots")
        for(Map slot : slots) {
            parseSlot(ctx, roomName, slot)
        }
    }

    private static void parseSlot(ParseContext ctx, String roomName, def json) {
        if(json.talk) {
            String speakerName = json.talk.speaker
            if (!ctx.speakers.containsKey(speakerName)) {
                Speaker speaker = Speaker.builder()
                        .id(speakerName)
                        .name(speakerName)
                        .firstname(speakerName.split(" ")[0])
                        .lastname(speakerName.split(" ")[1])
                        .bio((String) json.talk.bio)
                        .build()
                ctx.speakers.put(speakerName, speaker)
            }
            if (!ctx.locations.containsKey(roomName)) {
                Location location = Location.builder()
                        .id(roomName)
                        .order(1)
                        .names([EN: roomName])
                        .build()
                ctx.locations.put(roomName, location)
            }
            String trackName = json.talk.category
            if (!ctx.tracks.containsKey(trackName)) {
                Track track = Track.builder()
                        .id(trackName)
                        .order(1)
                        .names([EN: trackName])
                        .build()
                ctx.tracks.put(trackName, track)
            }
            String eventType = json.talk.ttype
            if (!ctx.eventTypes.containsKey(trackName)) {
                EventType type = EventType.builder()
                        .id(eventType)
                        .order(1)
                        .names([EN: eventType])
                        .build()
                ctx.eventTypes.put(eventType, type)
            }

            String eventId = json.talk.id
            Event event = Event.builder()
                    .id(eventId)
                    .track(ctx.tracks.get(trackName))
                    .type(ctx.eventTypes.get(eventType))
                    .location(ctx.locations.get(roomName))
                    .start(LocalDateTime.ofInstant(
                        new Date(Long.valueOf((String) json.starttime)).toInstant(), ZoneId.systemDefault()))
                    .end(LocalDateTime.ofInstant(
                        new Date(Long.valueOf((String) json.endtime)).toInstant(), ZoneId.systemDefault()))
                    .speakers(Collections.singletonList(ctx.speakers.get(speakerName)))
                    .language(ctx.languages.get("EN"))
                    .title((String) json.talk.title)
                    .abstractText((String) json.talk.description)
                    .build()
            ctx.events.put(eventId, event)
        }
    }

    private static class ParseContext {
        private Map<String, Event> events = new HashMap<>()
        private Map<String, Location> locations = new HashMap<>()
        private Map<String, Speaker> speakers = new HashMap<>()
        private Map<String, Track> tracks = new HashMap<>()
        private Map<String, EventType> eventTypes = new HashMap<>()
        private Map<String, Language> languages = new HashMap<>()
    }

}
