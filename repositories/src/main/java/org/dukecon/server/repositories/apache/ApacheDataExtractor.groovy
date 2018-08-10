package org.dukecon.server.repositories.apache

import com.timgroup.jgravatar.Gravatar
import com.timgroup.jgravatar.GravatarDefaultImage
import com.timgroup.jgravatar.GravatarRating
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.text.WordUtils
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
        log.debug("Extracting data for '{}'", config)
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
        ctx.languages.put("en", new Language("en", "en", 1, [en: "English"], null))
        parseRooms(ctx, conferenceJson)

        MetaData metaData = MetaData.builder()
                .languages(new ArrayList<Speaker>(ctx.languages.values()))
                .eventTypes(new ArrayList<Speaker>(ctx.eventTypes.values()))
                .tracks(new ArrayList<Speaker>(ctx.tracks.values()))
                .locations(new ArrayList<Speaker>(ctx.locations.values()))
                .audiences(new ArrayList<Speaker>(ctx.audiences.values()))
                .build()
        Conference conference = Conference.builder().id(conferenceId)
                .name(conferenceName)
                .url(conferenceUrl)
                .homeUrl(conferenceHomeUrl)
                .events(new ArrayList<Event>(ctx.events.values()))
                .speakers(new ArrayList<Speaker>(ctx.speakers.values()))
                .metaData(metaData)
                .build()

        // Tweak the import ...
        postProcess(conference)

        return conference
    }

    private static void parseRooms(ParseContext ctx, def json) {
        List<Map> rooms = (List<Map>) json
        for (Map room : rooms) {
            String roomName = room.get("name")
            List<Map> days = (List<Map>) room.get("days")
            for (Map day : days) {
                parseDay(ctx, roomName, day)
            }
        }
    }

    private static void parseDay(ParseContext ctx, String roomName, def json) {
        if (json.slots) {
            List<Map> slots = json.slots
            for (Map slot : slots) {
                parseSlot(ctx, roomName, slot)
            }
        }
    }

    private static void parseSlot(ParseContext ctx, String roomName, def json) {
        if (json.talk) {
            String speakerName = json.talk.speaker
            String[] speakers = speakerName.split(",")
            String[] speakerBios = ((String) json.talk.bio).split("\\|")
            List<Speaker> curTalksSpeakers = new LinkedList<>()
            for (int i = 0; i < speakers.length; i++) {
                String speakerString = speakers[i].trim()
                String speakerBio = speakerBios[Math.min(i, speakerBios.length - 1)].trim()
                if (!ctx.speakers.containsKey(speakerString)) {
                    String firstName
                    String lastName
                    if (speakerString.contains(" ")) {
                        firstName = WordUtils.capitalizeFully(speakerString.split(" ")[0].trim())
                        lastName = WordUtils.capitalizeFully(speakerString.substring(firstName.length()).trim())
                    } else {
                        firstName = WordUtils.capitalizeFully(speakerString)
                        lastName = ""
                    }
                    Speaker speaker = Speaker.builder()
                            .id(speakerString)
                            .name(speakerString)
                            .firstname(firstName)
                            .lastname(lastName)
                            .bio(speakerBio)
                            .build()
                    ctx.speakers.put(speakerString, speaker)
                    curTalksSpeakers.add(speaker)
                } else {
                    curTalksSpeakers.add(ctx.speakers.get(speakerString))
                }
                // If this is the first speaker, set the email address.
                if ((i == 0) && (ctx.speakers.get(speakerString).getEmail() == null)) {
                    String email = json.email
                    if (email != null) {
                        ctx.speakers.get(speakerString).setEmail(email)
                    }
                }
            }
            if (!ctx.locations.containsKey(roomName)) {
                Location location = Location.builder()
                        .id(roomName)
                        .order(1)
                        .names([en: roomName])
                        .capacity(getCapacity(roomName))
                        .build()
                ctx.locations.put(roomName, location)
            }
            String trackName = json.talk.category
            if (!ctx.tracks.containsKey(trackName)) {
                Track track = Track.builder()
                        .id(trackName)
                        .order(1)
                        .names([en: trackName])
                        .build()
                ctx.tracks.put(trackName, track)
            }
            String eventType = json.talk.ttype
            if (!ctx.eventTypes.containsKey(trackName)) {
                EventType type = EventType.builder()
                        .id(eventType)
                        .order(1)
                        .names([en: eventType])
                        .build()
                ctx.eventTypes.put(eventType, type)
            }
            if (!ctx.audiences.containsKey("dev")) {
                Audience audience = Audience.builder()
                        .id("dev")
                        .order(1)
                        .names([en: "Devlopers"])
                        .build()
                ctx.audiences.put("dev", audience)
            }

            String eventId = json.talk.id

            Event event = Event.builder()
                    .id(eventId)
                    .track(ctx.tracks.get(trackName))
                    .type(ctx.eventTypes.get(eventType))
                    .location(ctx.locations.get(roomName))
                    .start(LocalDateTime.ofInstant(
                    new Date((Long.valueOf((String) json.starttime) - 86400) * 1000).toInstant(), ZoneId.systemDefault()))
                    .end(LocalDateTime.ofInstant(
                    new Date((Long.valueOf((String) json.endtime) - 86400) * 1000).toInstant(), ZoneId.systemDefault()))
                    .speakers(curTalksSpeakers)
                    .language(ctx.languages.get("en"))
                    .audience(ctx.audiences.get("dev"))
                    .title((String) json.talk.title)
                    .abstractText((String) json.talk.description)
                    .documents(new HashMap<String, String>())
                    .build()

            // Check if a zip with presentation content exists. If if does, add a document to the list.
            String presentationUrl = "https://apachecon.com/acna18/presentations/" + eventId + ".zip"
            if (getResponseCode(presentationUrl) == HttpURLConnection.HTTP_OK) {
                event.documents = [Presentation: presentationUrl]
            }

            for (Speaker speaker : curTalksSpeakers) {
                if (speaker.events == null) {
                    speaker.events = new LinkedList<>()
                }
                speaker.events.add(event)
            }

            String pictureUrl = json.talk.photo
            if (pictureUrl != null) {
                // We are mis-using this field as we are resetting it back to null later on
                // It would be great if it was possible to add properties to Speakers.
                curTalksSpeakers.get(0).email = pictureUrl
            }

            ctx.events.put(eventId, event)
        }
    }

    private static int getCapacity(String roomName) {
        switch (roomName) {
            case "Ballroom": return 250
            case "Keynotes": return 250
            case "Viger A": return 60
            case "Viger B": return 60
            case "Viger C": return 60
            case "Terrasse": return 30
            default: return 100
        }
    }

    private void postProcess(Conference conference) {
        for (Speaker speaker : conference.speakers) {
            if (speaker.email != null) {
                byte[] image = null;
                if (speaker.email.contains("@")) {
                    // Try to get Gravatar images for each speaker with an email.
                    Gravatar gravatar = new Gravatar().setSize(275)
                            .setRating(GravatarRating.GENERAL_AUDIENCES)
                            .setDefaultImage(GravatarDefaultImage.HTTP_404)
                    image = gravatar.download(speaker.email)
                } else if (speaker.email.startsWith("http")) {
                    // Try to fetch the image.
                    image = downloadUrl(speaker.email)
                }
                if(image != null) {
                    speaker.photoId = speakerImageService.addImage(image)
                }
                // Reset the email as we don't want to display it.
                speaker.email = null
            }
        }

        // Sort the location names
        Map<String, Location> locations = new TreeMap<>(new AlphabeticalComparator())
        for (Location location : conference.metaData.locations) {
            locations.put(location.names.get("en"), location)
        }
        int i = 0
        for (Map.Entry<String, Location> entry : locations.entrySet()) {
            entry.value.order = i
            i++
        }

        // Sort the track names
        Map<String, Track> tracks = new TreeMap<>(new AlphabeticalComparator())
        for (Track track : conference.metaData.tracks) {
            tracks.put(track.names.get("en"), track)
        }
        i = 0
        for (Map.Entry<String, Track> entry : tracks.entrySet()) {
            entry.value.order = i
            i++
        }
    }

    private static class ParseContext {
        private Map<String, Audience> audiences = new HashMap<>()
        private Map<String, Event> events = new HashMap<>()
        private Map<String, EventType> eventTypes = new HashMap<>()
        private Map<String, Location> locations = new HashMap<>()
        private Map<String, Speaker> speakers = new HashMap<>()
        private Map<String, Track> tracks = new HashMap<>()
        private Map<String, Language> languages = new HashMap<>()
    }

    private static class AlphabeticalComparator implements Comparator<String> {
        @Override
        int compare(String o1, String o2) {
            if (o1 == null) {
                return -1
            }
            if (o2 == null) {
                return 1
            }
            if (o1.equals(o2)) {
                return 0
            }
            return o1.compareToIgnoreCase(o2)
        }
    }

    private static int getResponseCode(String urlString) throws MalformedURLException, IOException {
        URL u = new URL(urlString)
        HttpURLConnection huc = (HttpURLConnection) u.openConnection()
        huc.setRequestMethod("GET")
        huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)")
        huc.connect()
        return huc.getResponseCode()
    }

    private static byte[] downloadUrl(String url) {
        URL u = new URL(url);
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        InputStream is = null
        try {
            is = u.openStream()
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n

            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n)
            }
        }
        catch (IOException e) {
            System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage())
            e.printStackTrace()
            // Perform any other exception handling that's appropriate.
        }
        finally {
            if (is != null) {
                is.close()
            }
        }
        return baos.toByteArray()
    }

}
