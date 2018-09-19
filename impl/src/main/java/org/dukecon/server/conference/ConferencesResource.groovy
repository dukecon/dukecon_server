package org.dukecon.server.conference

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateException
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.dukecon.model.Conference
import org.dukecon.model.Styles
import org.dukecon.server.repositories.ConferenceDataProvider
import org.dukecon.services.ConferenceService
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseBody

import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.time.format.DateTimeFormatter

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@Path("conferences")
@Api(value = "/", description = "Conferences endpoint")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class ConferencesResource {
    private final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE

    ConferenceService conferenceService
//    Map<String, ConferenceDataProvider> talkProviders = new HashMap<>()
    private final ConferencesConfigurationService configurationService

    @Inject
    ConferencesResource(ConferenceService conferenceService, ConferencesConfigurationService configurationService, List<ConferenceDataProvider> talkProviders) {
        this.configurationService = configurationService
        this.conferenceService = conferenceService
//        talkProviders.each { this.talkProviders[it.conferenceId] = it }
    }

    @GET
    @ApiOperation(value = "returns list of conferences",
            response = Conference.class,
            responseContainer = "List")
    Response getAllConferences() {
        def conferences = configurationService.conferences.collect { c -> [id: c.id, name: c.name, year: c.year, url: c.url, homeUrl: c.homeUrl, homeTitle: c.homeTitle, startDate: dtf.format(c.startDate), endDate: dtf.format(c.endDate)] }
        return Response.ok().entity(conferences).build()
    }

    /**
     * Reread conference input data.
     *
     * @param id
     * @return
     */
    @GET
    @Path("update/{id}")
    Response updateConference(@PathParam("id") String id) {
        try {
            if (conferenceService.getConference(id) == null)
                return Response.status(Response.Status.NOT_FOUND).build()
            if (conferenceService.refreshConference(id)) {
                return Response.ok().entity([message: "ok"]).build()
            }
//            if (talkProviders[id].isBackupActive()) {
//                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
//                        .entity([message: "backup active"]).build()
//            }
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
//                    .entity([message: talkProviders[id].staleException.toString()])
                    .build()
        } catch (RuntimeException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity([message: e.toString()]).build()
        }
    }

    @Path("{id}")
    @ApiOperation(value = "Conference details")
    ConferenceDetailResource getConferenceDetails(@PathParam("id") String id) {
        def conference = conferenceService.read(id.replace(".json", ""))
        if (conference == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND)
        }
        return new ConferenceDetailResource(conference)
    }

    @GET
    @ResponseBody
    @Produces("text/css")
    @Path("{id}/styles.css")
    @ApiOperation(value = "Conference styles")
    String getConferenceStyles(@PathParam("id") String id) {
        Styles styles = conferenceService.getConferenceStyles(id)
        if (styles == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND)
        }
        try {
            final Configuration cfg = new Configuration()
            cfg.setClassForTemplateLoading(getClass(), "/templates")

            // Get the template instance.
            final Template temp = cfg.getTemplate("styles.ftl")

            // Create the template output.
            final StringWriter out = new StringWriter()
            final Map<String, Object> enhancedVarMap = new HashMap<String, Object>()
            enhancedVarMap.put("styles", styles)
            temp.process(enhancedVarMap, out)
            out.flush()

            // Extract the email content as String.
            // The first line contains the subject, the rest the mail content.
            return out.getBuffer().toString()
        } catch (final TemplateException e) {
            if (log.isWarnEnabled()) {
                log.warn("Error creating email", e)
            }
            throw new RuntimeException("ERROR_CREATING_STYLE_CSS", e)
        } catch (final IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Error creating email", e)
            }
            throw new RuntimeException("ERROR_CREATING_STYLE_CSS", e)
        }
    }
}
