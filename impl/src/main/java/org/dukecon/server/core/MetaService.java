package org.dukecon.server.core;

import org.dukecon.server.conference.ConferenceDataProvider;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Component
@Path("meta")
public class MetaService {
    @Inject
    List<ConferenceDataProvider> talkProviders;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMeta() {
        String id = "499959"; //hardcoded to Javaland for now
        Optional<ConferenceDataProvider> provider = talkProviders.stream().filter( p -> p.getConference().getId().equals(id)).findFirst();
        return Response.ok().entity(provider.get().getConference().getMetaData()).build();
    }

    @GET
    @Path("ping")
    public Response ping() {
        Map<String, String> m = new HashMap<>(1);
        m.put("last_updated", "2015-05-19T16:20:11");
        return Response.ok().entity(m).build();
    }
}
