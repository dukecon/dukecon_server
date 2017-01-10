package org.dukecon.server.conference

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateException
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import org.dukecon.model.Conference
import org.dukecon.model.Styles
import org.dukecon.server.adapter.ConferenceDataProvider
import org.dukecon.server.adapter.doag.DoagSpeakerImageService
import org.dukecon.services.ConferenceService
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseBody

import javax.inject.Inject
import javax.ws.rs.*
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
    private final DoagSpeakerImageService speakerImageService

    @Inject
    SpeakerImageResource(DoagSpeakerImageService speakerImageService) {
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
        DoagSpeakerImageService.ImageWithName image = speakerImageService.getImage(id)
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
