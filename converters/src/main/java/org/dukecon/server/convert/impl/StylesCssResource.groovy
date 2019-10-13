package org.dukecon.server.convert.impl

import groovy.text.SimpleTemplateEngine
import groovy.text.TemplateEngine
import org.dukecon.server.conference.ConferencesConfiguration
import org.dukecon.server.convert.ResourceFileProvider

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class StylesCssResource implements ResourceFileProvider<String> {

    private final String templateFilename;

    private TemplateEngine templateEngine = new SimpleTemplateEngine()

    private static final Map<String, String> DEFAULTSTYLES = [
            dark     : '#1aa3b1',
            darkLink : '#1aa3b1',
            hover    : '#00c3d7',
            hoverLink: '#00c3d7',
            reverse  : '#fff',
            highlight: '#ddee55',
            alternate: '#bf5a00',
    ]
    private final Map<String, String> styles
    private final String conferenceId

    StylesCssResource(String conferenceId, Map<String, String> styles, String templateFilename) {
        this.conferenceId = conferenceId
        this.styles = styles ?: [:]
        this.templateFilename = templateFilename
    }

    @Override
    String getFileName() {
        return String.format("conferences/%s/styles.css", conferenceId);
    }

    String getContent() {
        return templateEngine.createTemplate(getTemplateContent()).make([styles: DEFAULTSTYLES << styles]);
    }

    private String getTemplateContent() {
        this.getClass().getResource(templateFilename).text
    }
}
