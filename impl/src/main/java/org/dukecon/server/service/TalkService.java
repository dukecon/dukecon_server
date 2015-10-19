package org.dukecon.server.service;

import org.dukecon.model.TalkOld;
import org.dukecon.server.conference.JavalandDataProvider;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Component
@Path("talks")
@Deprecated
public class TalkService {

    @Inject
    JavalandDataProvider talkProvider;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public Response getTalks() {
        Collection<TalkOld> talks = talkProvider.getAllTalks();
        return Response.ok().entity(talks).build();
    }
}
