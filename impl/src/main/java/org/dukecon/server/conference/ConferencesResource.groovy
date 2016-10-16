package org.dukecon.server.conference

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateException
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.dukecon.model.Styles
import org.dukecon.services.ConferenceService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.ServletContextAware

import javax.inject.Inject
import javax.servlet.ServletContext
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import io.swagger.annotations.*

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Component
@Path("conferences")
@Api(value="/", description = "Conferences endpoint")
@Produces(MediaType.APPLICATION_JSON)
@TypeChecked
@Slf4j
class ConferencesResource implements ServletContextAware {
    ConferenceService conferenceService
    List<ConferenceDataProvider> talkProviders
    ServletContext servletContext

    @Inject
    ConferencesResource(ConferenceService conferenceService, List<ConferenceDataProvider> talkProviders) {
        this.conferenceService = conferenceService
        this.talkProviders = talkProviders
    }

    @Override
    void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext
    }

    @GET
    @ApiOperation(value="returns list of conferences",
            response = Conference.class,
            responseContainer = "List")
    public Response getAllConferences() {
        def conferences = getConferences().collect{c -> [id : c.id, name : c.name]}
        return Response.ok().entity(conferences).build()
    }

    private List<Conference> getConferences() {
        talkProviders.findAll{p -> p.conference}.collect{p -> p.conference}
    }

    /**
     * Reread conference input data.
     *
     * @param id
     * @return
     */
    @GET
    @Path("update/{id:[0-9]*}")
    Response updateConference(@PathParam("id") String id) {
        try {
            def provider = getConferenceProvider(id)
            if (provider == null)
                return Response.status(Response.Status.NOT_FOUND).build()
            if (provider.update()) {
                return Response.ok().entity([message: "ok"]).build()
            }
            if (provider.isBackupActive()) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity([message: "backup active"]).build()
            }
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity([message: provider.staleException.toString()]).build()
        } catch (RuntimeException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity([message: e.toString()]).build()
        }
    }

    @Path("{id}")
    @ApiOperation(value = "Conference details")
    public ConferenceDetailResource getConferenceDetails(@PathParam("id") String id) {
        def conference = getConferences().find{c -> c.id == id.replace(".json", "")}
        if (conference == null) {
            log.warn("Conference with id {} not found", id)
            return new ConferenceDetailResource(null)
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
        if(styles == null) {
            throw new ResourceNotFoundException()
        }
        try {
            final Configuration cfg = new Configuration()
            cfg.setServletContextForTemplateLoading(servletContext, "/WEB-INF/templates")

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

    private ConferenceDataProvider getConferenceProvider(String id) {
        return talkProviders.find{p -> p.conference?.id == id}
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    class ResourceNotFoundException extends RuntimeException {
    }

}
