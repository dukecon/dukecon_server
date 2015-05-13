package org.dukecon.server.service;

import org.dukecon.model.Conference;
import org.dukecon.model.MetaData;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Component
@Path("meta")
public class MetaService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMeta() {
        Conference conference = Conference.builder()
                .name("DukeCon Demo Workshop")
                .url("http://dukecon.org/demo")
                .build();
        MetaData metaData = MetaData.builder().conference(conference).build();

        return Response.ok().entity(metaData).build();
    }
}
