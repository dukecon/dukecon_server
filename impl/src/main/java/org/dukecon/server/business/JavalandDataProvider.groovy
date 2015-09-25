package org.dukecon.server.business

import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.dukecon.model.MetaData
import org.dukecon.model.Speaker
import org.dukecon.model.Talk
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.time.Instant

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Slf4j
@Component
@TypeChecked
class JavalandDataProvider {

	@Value("\${talks.uri:https://www.javaland.eu/api/schedule/JavaLand2016/jl.php?key=TestJL}")
	String talksUri

	@Value("\${talks.cache.expires:3600}")
	Integer cacheExpiresAfterSeconds

	@Value("\${conference.url:http://dukecon.org}")
	String conferenceUrl

	@Value("\${conference.name:DukeCon Conference}")
	String conferenceName

	private Instant cacheLastUpdated

	List<Talk> talks = []
	List<Talk> talksV2 = []
	MetaData metaData

	MetaData getMetaData() {
		checkCache()
		return metaData
	}

	Collection<Talk> getAllTalksWithReplaceMetaData() {
		checkCache()
		return talksV2
	}

	Collection<Talk> getAllTalks() {
		checkCache()
		return talks
	}

	private void checkCache() {
		if (talks.isEmpty() || !metaData || isCacheExpired()) {
            clearCache()
			cacheLastUpdated = Instant.now()
			log.info("Reread talks from '{}'", talksUri)
			readTalks()
		}
	}

    public void clearCache() {
        this.talks = []
        this.talksV2 = []
        this.metaData = null
    }

	private boolean isCacheExpired() {
		if(!cacheExpiresAfterSeconds) {
			return true
		}
		return cacheLastUpdated.plusSeconds(cacheExpiresAfterSeconds).isBefore(Instant.now())
	}

	@TypeChecked(TypeCheckingMode.SKIP)
	protected void readTalks() {
		def input = talksUri.startsWith("resource:") ? readResource() : readJavalandFile()
		JsonSlurper slurper = new JsonSlurper()
		def rawTalksJson = slurper.parse(input, "ISO-8859-1").hits.hits._source
		metaData = createMetaData(rawTalksJson)
		talks = convertFromRaw(rawTalksJson)
		talksV2 = convertFromRaw(rawTalksJson, true)
	}

	private InputStream readResource() {
		log.info ("Reading JSON data from local file")
		String[] resourceParts = talksUri.split(":")
		return this.getClass().getResourceAsStream(resourceParts[1])
	}

	private URL readJavalandFile() {
		log.info ("Reading JSON data from remote '{}'", talksUri)
		return new URL(talksUri)
	}

	@TypeChecked(TypeCheckingMode.SKIP)
	private List<Talk> convertFromRaw(List talks, boolean v2 = false) {
		Set<String> talkIds = new HashSet<>()
		return talks.collect { t ->
			String id = t.ID.toString()
			if (talkIds.contains(id)) {
				log.error("Duplicate Talk ID '{}' in raw data!", id)
				return
			}
			Speaker speaker = Speaker.builder().name(t.REFERENT_NAME).company(t.REFERENT_FIRMA).defaultSpeaker(true).build()
			Speaker speaker2 = t.COREFERENT_NAME == null ? null : Speaker.builder().name(t.COREFERENT_NAME).company(t.COREFERENT_FIRMA).build()
			List<Speaker> speakers = [speaker]
			if (speaker2) {
				speakers.add(speaker2)
			}
			def builder = Talk.builder()
					.id(id)
					.start(t.DATUM_ES_EN + 'T' + t.BEGINN)
					.end(t.DATUM_ES_EN + 'T' + t.ENDE)
					.title(t.TITEL)
					.abstractText(t.ABSTRACT_TEXT?.replaceAll("&quot;", "\""))
					.language(t.SPRACHE)
					.demo(t.DEMO != null && t.DEMO.equalsIgnoreCase('ja'))
					.speakers(speakers)
			if (v2) {
				builder.trackNumber(metaData.getOrderFromTrackName(t.TRACK ?: ''))
                builder.roomNumber(metaData.getOrderFromRoomName(t.RAUMNAME ?: ''))
                builder.levelNumber(metaData.getOrderFromAudienceName(t.AUDIENCE ?: ''))
                builder.typeNumber(metaData.getOrderFromTalkTypeName(t.VORTRAGSTYP ?: ''))
			} else {
				builder.track(t.TRACK)
						.level(t.AUDIENCE)
						.type(t.VORTRAGSTYP)
						.location(t.RAUMNAME)
			}
			return builder.build()
		}
	}

	MetaData createMetaData(rawJson) {
		MetaDataExtractor extractor = new MetaDataExtractor(talksJson: rawJson, conferenceName: conferenceName, conferenceUrl: conferenceUrl)
		return MetaData.builder().conference(extractor.conference).rooms(extractor.rooms).tracks(extractor.tracks).languages(extractor.languages).defaultLanguage(extractor.defaultLanguage).audiences(extractor.audiences).talkTypes(extractor.talkTypes).build()
	}
}
