package org.dukecon.server.services;

import org.dukecon.model.Conference;
import org.dukecon.services.ConferenceService;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Created by christoferdutz on 02.08.16.
 */

@Service("conferenceService")
@RemotingDestination
public class ConferenceServiceImpl implements ConferenceService {

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
        System.out.println("list(Conference)");
        return null;
    }

}
