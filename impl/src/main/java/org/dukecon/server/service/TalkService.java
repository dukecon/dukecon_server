package org.dukecon.server.service;

import org.dukecon.model.Talk;
import org.dukecon.server.business.TalkProvider;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Component
@Path("talks")
public class TalkService {

    @Inject
    TalkProvider talkProvider;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTalks() {
        List<Talk> talks = talkProvider.getAllTalks();
        return Response.ok().entity(talks).build();
    }
}
