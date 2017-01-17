package org.dukecon.server.speaker

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@Path("speaker/images")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class SpeakerImageResource {
    private final SpeakerImageService speakerImageService

    @Inject
    SpeakerImageResource(SpeakerImageService speakerImageService) {
        this.speakerImageService = speakerImageService
    }

    @GET
    @Path("/")
    Response getAllConferences() {
        return Response.ok().entity(speakerImageService.images.collect {k, v -> [filename: v.filename, id: k]}).build()
    }

    @GET
    @Path('{id}')
    Response getConferenceDetails(@PathParam("id") String id) {
        SpeakerImageService.ImageWithName image = speakerImageService.getImage(id)
        if (image) {
            return Response.status(Response.Status.OK)
                    .type("image/${image.filename.tokenize('.').last()}")
                    .header('Content-Disposition', /filename="${image.filename}"/)
                    .header('Content-Length', String.valueOf(image.content.length))
                    .entity(image.content)
                    .build()
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
