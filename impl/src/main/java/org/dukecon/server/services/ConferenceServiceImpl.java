package org.dukecon.server.services;

import org.dukecon.model.*;
import org.dukecon.server.conference.ConferenceDataProvider;
import org.dukecon.services.ConferenceService;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

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
            if(conference == null) {
                continue;
            }

            // When reading things from the input json there are multiple instances of many entities,
            // This code makes sure only one entity instance is used throughout the object graph.
            Map<String, Audience> audienceMap = new HashMap<>();
            Map<String, EventType> eventTypeMap = new HashMap<>();
            Map<String, Language> languageMap = new HashMap<>();
            Map<String, Location> locationMap = new HashMap<>();
            Map<String, Speaker> speakerMap = new HashMap<>();
            Map<String, Track> trackMap = new HashMap<>();
            Map<String, Event> eventMap = new HashMap<>();
            if(conference.getMetaData().getAudiences() != null) {
                for (Audience audience : conference.getMetaData().getAudiences()) {
                    audienceMap.put(audience.getId(), audience);
                }
            }
            if(conference.getMetaData().getEventTypes() != null) {
                for (EventType eventType : conference.getMetaData().getEventTypes()) {
                    eventTypeMap.put(eventType.getId(), eventType);
                }
            }
            if(conference.getMetaData().getLanguages() != null) {
                for (Language language : conference.getMetaData().getLanguages()) {
                    languageMap.put(language.getId(), language);
                }
            }
            conference.getMetaData().setDefaultLanguage(languageMap.get(conference.getMetaData().getDefaultLanguage().getId()));
            if(conference.getMetaData().getLocations() != null) {
                for (Location location : conference.getMetaData().getLocations()) {
                    locationMap.put(location.getId(), location);
                }
            }
            if(conference.getSpeakers() != null) {
                for (Speaker speaker : conference.getSpeakers()) {
                    speakerMap.put(speaker.getId(), speaker);
                }
            }
            if(conference.getMetaData().getTracks() != null) {
                for (Track track : conference.getMetaData().getTracks()) {
                    trackMap.put(track.getId(), track);
                }
            }
            if(conference.getEvents() != null) {
                for (Event event : conference.getEvents()) {
                    if (event.getAudience() != null) {
                        event.setAudience(audienceMap.get(event.getAudience().getId()));
                    }
                    if (event.getTrack() != null) {
                        event.setTrack(trackMap.get(event.getTrack().getId()));
                    }
                    if (event.getLanguage() != null) {
                        event.setLanguage(languageMap.get(event.getLanguage().getId()));
                    }
                    if (event.getLocation() != null) {
                        event.setLocation(locationMap.get(event.getLocation().getId()));
                    }
                    if (event.getType() != null) {
                        event.setType(eventTypeMap.get(event.getType().getId()));
                    }
                    List<Speaker> speakers = event.getSpeakers().stream().map(
                            speaker -> speakerMap.get(speaker.getId())).collect(Collectors.toCollection(LinkedList::new));
                    event.setSpeakers(speakers);
                    eventMap.put(event.getId(), event);
                }
            }
            if(conference.getSpeakers() != null) {
                for (Speaker speaker : conference.getSpeakers()) {
                    if (speaker.getEvents() != null) {
                        List<Event> events = speaker.getEvents().stream().map(
                                event -> eventMap.get(event.getId())).collect(Collectors.toCollection(LinkedList::new));
                        speaker.setEvents(events);
                    }
                }
            }
            conference.getMetaData().setId(conference.getId());

            conferences.add(conference);
        }
        return conferences;
    }

}
