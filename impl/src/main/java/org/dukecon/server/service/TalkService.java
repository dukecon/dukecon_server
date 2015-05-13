package org.dukecon.server.service;

import org.dukecon.model.Talk;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Component
@Path("talks")
public class TalkService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTalks() {
        List<Talk> talks = new ArrayList<>();

        // you fill the talks list here...

        return Response.ok().entity(talks).build();
    }
}
