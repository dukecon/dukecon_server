package org.dukecon.server.services;

import org.dukecon.model.Conference;
import org.dukecon.model.Event;
import org.dukecon.model.Language;
import org.dukecon.server.conference.ConferenceDataProvider;
import org.dukecon.services.ConferenceService;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
