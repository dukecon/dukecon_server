package org.dukecon.server.service;

import org.dukecon.model.Conference;
import org.dukecon.model.MetaData;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

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

    @GET
    @Path("ping")
    public Response ping() {
        Map<String, String> m = new HashMap<>(1);
        m.put("last_updated", "2015-05-19T16:20:11");
        return Response.ok().entity(m).build();
    }

    @GET
    @Path("pingSecure")
    public Response pingSecure() {
        Map<String, String> m = new HashMap<>(1);
        m.put("last_updated", "2015-05-19T16:20:11");
        return Response.ok().entity(m).build();
    }
}
