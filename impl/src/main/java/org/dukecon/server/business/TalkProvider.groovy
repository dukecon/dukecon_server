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

	@Value("\${workLocal:false}")
	private boolean workLocal
	
	private String javalandTalksURL = 'https://www.javaland.eu/api/schedule/JavaLand2015/jl.php?key=TestJL'

    private List<Talk> talks = []

    List<Talk> getAllTalks() {
        if (talks.isEmpty()) {
			if (workLocal) {
				readDemoFile()
			} else {
            	readJavalandFile()
			}
        }
        return talks
    }

    private void readDemoFile() {
		log.info ("Reading JSON data from local file")
        ObjectMapper mapper = new ObjectMapper()
        InputStream is = this.getClass().getResourceAsStream('/demotalks.json')
        JsonSlurper slurper = new JsonSlurper()
        def json = slurper.parse(is)
        json.each {
            talks.add(mapper.convertValue(it, Talk.class))
        }
    }

	@TypeChecked(TypeCheckingMode.SKIP)
    private void readJavalandFile() {
		log.info ("Reading JSON data from remote '{}'", javalandTalksURL)
        URL javaland = new URL(javalandTalksURL)
        JsonSlurper slurper = new JsonSlurper()
        def json = slurper.parse(javaland, "ISO-8859-1")
        json.hits.hits.each {
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
