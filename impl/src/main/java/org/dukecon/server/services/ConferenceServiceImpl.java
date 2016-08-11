package org.dukecon.server.services;

import org.dukecon.model.*;
import org.dukecon.server.conference.ConferenceDataProvider;
import org.dukecon.services.ConferenceService;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by christoferdutz on 02.08.16.
 */

@Service("conferenceService")
@RemotingDestination
public class ConferenceServiceImpl implements ConferenceService {

    @Inject
    private List<ConferenceDataProvider> talkProviders;

    @Override
    public void create(Conference obj) {
        System.out.println("create(Conference)");
    }

    @Override
    public Conference read(String id) {
        System.out.println("read(Conference)");
        return null;
    }

    @Override
    public void update(Conference obj) {
        System.out.println("update(Conference)");
    }

    @Override
    public void delete(Conference obj) {
        System.out.println("delete(Conference)");
    }

    @Override
    public void delete(String id) {
        System.out.println("delete(Conference)");
    }

    @Override
    public Collection<Conference> list() {
        Collection<Conference> conferences = new LinkedList<>();
        for(ConferenceDataProvider provider : talkProviders) {
            Conference conference = provider.getConference();
            // TODO : Remove this hack!
            updateLanguageIds(conference.getDefaultLanguage());
            for(Language language : conference.getLanguages()) {
                updateLanguageIds(language);
            }
            for(Event event : conference.getEvents()) {
                updateLanguageIds(event.getLanguage());
            }

            Map<String, Audience> audienceMap = new HashMap<>();
            Map<String, EventType> eventTypeMap = new HashMap<>();
            Map<String, Language> languageMap = new HashMap<>();
            Map<String, Location> locationMap = new HashMap<>();
            Map<String, Speaker> speakerMap = new HashMap<>();
            Map<String, Track> trackMap = new HashMap<>();
            Map<String, Event> eventMap = new HashMap<>();
            for(Audience audience : conference.getAudiences()) {
                audience.setConference(conference);
                audienceMap.put(audience.getId(), audience);
            }
            for(EventType eventType : conference.getEventTypes()) {
                eventType.setConference(conference);
                eventTypeMap.put(eventType.getId(), eventType);
            }
            for(Language language : conference.getLanguages()) {
                language.setConference(conference);
                languageMap.put(language.getId(), language);
            }
            conference.setDefaultLanguage(languageMap.get(conference.getDefaultLanguage().getId()));
            for(Location location : conference.getLocations()) {
                location.setConference(conference);
                locationMap.put(location.getId(), location);
            }
            for(Speaker speaker : conference.getSpeakers()) {
                speaker.setConference(conference);
                speakerMap.put(speaker.getId(), speaker);
            }
            for(Track track : conference.getTracks()) {
                track.setConference(conference);
                trackMap.put(track.getId(), track);
            }
            for(Event event : conference.getEvents()) {
                event.setConference(conference);
                if(event.getAudience() != null) {
                    event.setAudience(audienceMap.get(event.getAudience().getId()));
                }
                if(event.getTrack() != null) {
                    event.setTrack(trackMap.get(event.getTrack().getId()));
                }
                if(event.getLanguage() != null) {
                    event.setLanguage(languageMap.get(event.getLanguage().getId()));
                }
                if(event.getLocation() != null) {
                    event.setLocation(locationMap.get(event.getLocation().getId()));
                }
                if(event.getType() != null) {
                    event.setType(eventTypeMap.get(event.getType().getId()));
                }
                List<Speaker> speakers = new LinkedList<>();
                for(Speaker speaker : event.getSpeakers()) {
                    speakers.add(speakerMap.get(speaker.getId()));
                }
                event.setSpeakers(speakers);
                eventMap.put(event.getId(), event);
            }
            for(Speaker speaker : conference.getSpeakers()) {
                List<Event> events = new LinkedList<>();
                for(Event event : speaker.getEvents()) {
                    events.add(eventMap.get(event.getId()));
                }
                speaker.setEvents(events);
            }

            conferences.add(conference);
        }
        return conferences;
    }

    private void updateLanguageIds(Language language) {
        if(language != null) {
            switch (language.getId()) {
                case "de":
                    language.setId("1");
                    break;
                case "en":
                    language.setId("2");
                    break;
                case "germanenglish":
                    language.setId("3");
                    break;
            }
        }
    }
}
