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

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalField

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Slf4j
@Component
@TypeChecked
class TalkProvider {

	@Value("\${talks.uri:https://www.javaland.eu/api/schedule/JavaLand2015/jl.php?key=TestJL}")
	String talksUri

	@Value("\${talks.cache.expires:3600}")
	Integer cacheExpiresAfterSeconds

	private Instant cacheLastUpdated

	Map<String, Talk> talks = [:]

	Collection<Talk> getAllTalks() {
		log.debug("Reading talks from '{}'", talksUri)
		if (talks.isEmpty() || isCacheExpired()) {
			cacheLastUpdated = Instant.now()
			log.info("Reread talks from '{}'", talksUri)
			readTalks()
		}
		return talks.values()
	}

	protected void readTalks() {
		if (talksUri.startsWith("resource:")) {
			readResource()
		} else {
			readJavalandFile()
		}
	}

	private boolean isCacheExpired() {
		if(!cacheExpiresAfterSeconds) {
			return true
		}

		return cacheLastUpdated.plusSeconds(cacheExpiresAfterSeconds).isBefore(Instant.now())
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
				Talk talk = mapper.convertValue(it, Talk.class)
				if (talks.containsKey(talk.id)) {
					log.error ("Duplicate Talk ID '{}' in resource data!", talk.id)
				} else {
					talks[talk.id] = talk
				}
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
			String id = t.ID.toString()
			if (talks.containsKey(id)) {
				log.error ("Duplicate Talk ID '{}' in raw data!", id)
			} else {
				Speaker speaker = Speaker.builder().name(t.REFERENT_NAME).company(t.REFERENT_FIRMA).defaultSpeaker(true).build()
				Speaker speaker2 = t.COREFERENT_NAME == null ? null : Speaker.builder().name(t.COREFERENT_NAME).company(t.COREFERENT_FIRMA).build()
				List<Speaker> speakers = [speaker]
				if (speaker2) {
					speakers.add (speaker2)
				}
				Talk talk = Talk.builder()
						.id(id)
						.track(t.TRACK)
						.level(t.AUDIENCE)
						.type(t.VORTRAGSTYP)
						.start(t.DATUM_ES_EN + 'T' + t.BEGINN)
						.end(t.DATUM_ES_EN + 'T' + t.ENDE)
						.location(t.RAUMNAME)
						.title(t.TITEL)
						.abstractText(t.ABSTRACT_TEXT?.replaceAll("&quot;", "\""))
						.language(t.SPRACHE)
						.demo(t.DEMO != null && t.DEMO.equalsIgnoreCase('ja'))
						.speakers(speakers)
						.build()
				talks[id] = talk
			}
		}
	}
}
