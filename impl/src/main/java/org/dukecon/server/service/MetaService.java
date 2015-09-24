package org.dukecon.server.service;

import org.dukecon.model.Conference;
import org.dukecon.model.MetaData;
import org.dukecon.server.business.JavalandDataProvider;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
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
    @Inject
    JavalandDataProvider talkProvider;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMeta() {
        Conference conference = Conference.builder()
                .name("Javaland 2016")
                .url("http://dukecon.org/javaland")
                .build();
        MetaData metaData = talkProvider.getMetaData();
        metaData.setConference(conference);
        return Response.ok().entity(metaData).build();
    }

    @GET
    @Path("ping")
    public Response ping() {
        Map<String, String> m = new HashMap<>(1);
        m.put("last_updated", "2015-05-19T16:20:11");
        return Response.ok().entity(m).build();
    }
}
