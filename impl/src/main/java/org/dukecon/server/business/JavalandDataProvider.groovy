package org.dukecon.server.business

import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.dukecon.model.Speaker
import org.dukecon.model.Talk
import org.dukecon.model.TalkOld
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.time.Instant

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 * @author Falk Sippach, falk@jug-da.de, @sippsack
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

    List<TalkOld> talks = []
    Conference conference

    Conference getConference() {
        checkCache()
        return conference
    }

    Collection<TalkOld> getAllTalks() {
        checkCache()
        return talks
    }

    private void checkCache() {
        if (talks.isEmpty() || !conference || isCacheExpired()) {
            clearCache()
            cacheLastUpdated = Instant.now()
            log.info("Reread data from '{}'", talksUri)
            readData()
        }
    }

    public void clearCache() {
        this.talks = []
    }

    private boolean isCacheExpired() {
        if (!cacheExpiresAfterSeconds) {
            return true
        }
        return cacheLastUpdated.plusSeconds(cacheExpiresAfterSeconds).isBefore(Instant.now())
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    protected void readData() {
        def rawTalksJson = new JsonSlurper().parse(getInputData(), "ISO-8859-1").hits.hits._source
        conference = createConference(rawTalksJson)
        talks = convertFromRaw(rawTalksJson)
    }

    private Object getInputData() {
        talksUri.startsWith("resource:") ? readResource() : readJavalandFile()
    }

    private InputStream readResource() {
        log.info("Reading JSON data from local file")
        String[] resourceParts = talksUri.split(":")
        return this.getClass().getResourceAsStream(resourceParts[1])
    }

    private URL readJavalandFile() {
        log.info("Reading JSON data from remote '{}'", talksUri)
        return new URL(talksUri)
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private List<Talk> convertFromRaw(List talks) {
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
            def builder = TalkOld.builder()
                    .id(id)
                    .start(t.DATUM_ES_EN + 'T' + t.BEGINN)
                    .end(t.DATUM_ES_EN + 'T' + t.ENDE)
                    .title(t.TITEL)
                    .abstractText(t.ABSTRACT_TEXT?.replaceAll("&quot;", "\""))
                    .language(t.SPRACHE)
                    .demo(t.DEMO != null && t.DEMO.equalsIgnoreCase('ja'))
                    .speakers(speakers)
                    .track(t.TRACK)
                    .level(t.AUDIENCE)
                    .type(t.VORTRAGSTYP)
                    .location(t.RAUMNAME)
            return builder.build()
        }
    }

    Conference createConference(rawJson) {
        JavalandDataExtractor extractor = new JavalandDataExtractor(talksJson: rawJson, conferenceName: conferenceName, conferenceUrl: conferenceUrl)
        return extractor.buildConference()
    }
}
