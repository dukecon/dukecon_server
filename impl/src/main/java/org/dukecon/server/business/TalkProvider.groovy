package org.dukecon.server.business
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.dukecon.model.Speaker
import org.dukecon.model.Talk
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Slf4j
@Component
@TypeChecked
class TalkProvider {

    @Value("\${talks.uri:https://www.javaland.eu/api/schedule/JavaLand2015/jl.php?key=TestJL}")
	protected String talksUri

    protected List<Talk> talks = []

    List<Talk> getAllTalks() {
        log.debug("Reading talks from '{}'", talksUri)
        if (talks.isEmpty()) {
			if (talksUri.startsWith("resource:")) {
				readResource()
			} else {
            	readJavalandFile()
			}
        }
        return talks
    }

    private void readResource() {
		log.info ("Reading JSON data from local file")
        String[] resourceParts = talksUri.split(":")
        InputStream is = this.getClass().getResourceAsStream(resourceParts[1])
        JsonSlurper slurper = new JsonSlurper()
        if (talksUri.endsWith(".raw")) {
            def rawJson = slurper.parse(is, "ISO-8859-1")
            convertFromRaw(rawJson)
        } else {
            def json = slurper.parse(is)
            ObjectMapper mapper = new ObjectMapper()
            json.each {
                talks.add(mapper.convertValue(it, Talk.class))
            }
        }
    }

    private void readJavalandFile() {
		log.info ("Reading JSON data from remote '{}'", talksUri)
        URL javaland = new URL(talksUri)
        JsonSlurper slurper = new JsonSlurper()
        def rawJson = slurper.parse(javaland, "ISO-8859-1")
        convertFromRaw(rawJson)
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private Object convertFromRaw(rawJson) {
        rawJson.hits.hits.each {
            def t = it._source
            Speaker speaker = Speaker.builder().name(t.REFERENT_NAME).company(t.REFERENT_FIRMA).defaultSpeaker(true).build()
            Speaker speaker2 = t.COREFERENT_NAME == null ? null : Speaker.builder().name(t.COREFERENT_NAME).company(t.COREFERENT_FIRMA).build()
            Talk talk = Talk.builder()
                    .id(t.ID.toString())
                    .track(t.TRACK_EN)
                    .level(t.AUDIENCE_EN)
                    .type(t.VORTRAGSTYP_EN)
                    .start(t.DATUM_ES_EN + 'T' + t.BEGINN)
                    .end(t.DATUM_ES_EN + 'T' + t.ENDE)
                    .location(t.RAUMNAME)
                    .title(t.TITEL)
                    .abstractText(t.ABSTRACT_TEXT)
                    .language(t.SPRACHE)
                    .demo(t.DEMO != null && t.DEMO.equalsIgnoreCase('ja'))
                    .speakers([speaker, speaker2])
                    .build()
            talks.add(talk)
        }
    }

}
