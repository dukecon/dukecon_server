package org.dukecon.server.service;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonView;
import org.dukecon.model.Talk;
import org.dukecon.model.TalkOld;
import org.dukecon.server.business.JavalandDataProvider;
import org.springframework.stereotype.Component;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Component
@Path("talks")
public class TalkService {

    @Inject
    JavalandDataProvider talkProvider;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTalks() {
        Collection<TalkOld> talks = talkProvider.getAllTalks();
        return Response.ok().entity(talks).build();
    }

//    @JsonView()
    @GET
    @Path("v2")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTalks2() {
        Collection<Talk> talks = talkProvider.getConference().getTalks();
        return Response.ok().entity(talks).build();
    }
}
