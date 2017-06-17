package org.dukecon.server.adapter.sched

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.text.StrSubstitutor
import org.dukecon.model.Conference
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets

/**
 * Accessing remote resources and applying circuit breaker as needed.
 *
 * @deprecated will be removed in favor of WebResourceDataRemote
 *
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 * @author Alexander Schwartz, alexander.schwartz@gmx.net, @ahus1de
 *         (added Hystrix)
 * @author Christofer Dutz, christofer.dutz@codecentric.de, @ChristoferDutz
 */
@Slf4j
@Component
@TypeChecked
@Deprecated
class SchedDataRemote {

    @Value("\${sched.baseConferenceUrl:https://&{conferenceId}.sched.org/api/site/sync?api_key=&{conferenceApiKey}}")
    String baseConferenceUrl

    @Value("\${sched.baseEventsUrl:https://&{conferenceId}.sched.org/api/session/list?api_key=&{conferenceApiKey}&custom_data=Y&format=json}")
    String baseEventsUrl

    @Value("\${sched.backup:&{conferenceId}-backup.raw}")
    String backupPattern

    volatile boolean backupActive = false

    Exception staleException

    /* JsonOutput and JsonSlurper will encode/parse UTF characters as \\u anyway,
       but to be sure choosing UTF-8 here.
     */
    private final static String BACKUP_CHARSET = StandardCharsets.UTF_8.toString();

    @TypeChecked(TypeCheckingMode.SKIP)
    @HystrixCommand(groupKey = "sched", commandKey = "readConferenceData", fallbackMethod = "readConferenceDataFallback")
    public Conference readConferenceData(String conferenceNameAndKey) {
        try {
            // Prepare the rest url by replacing conference id and key in the template url.
            String[] parts = conferenceNameAndKey.split("@")
            if(parts.length != 2) {
                return null
            }
            String conferenceId = parts[0]
            String conferenceApiKey = parts[1]
            Map<String, String> conferenceConfig = new HashMap<>()
            conferenceConfig.put("conferenceId", conferenceId)
            conferenceConfig.put("conferenceApiKey", conferenceApiKey)
            String conferenceUri = StrSubstitutor.replace(baseConferenceUrl, conferenceConfig, "&{", "}")
            String eventsUri = StrSubstitutor.replace(baseEventsUrl, conferenceConfig, "&{", "}")
            String backupUri = StrSubstitutor.replace(backupPattern, conferenceConfig, "&{", "}")

            log.info("Rereading conference data from '{}' for conference '{}", baseConferenceUrl, conferenceId)
            def conferenceData = getInputData(conferenceUri)
            def conferenceRawJson = new JsonSlurper().parse(conferenceData, "ISO-8859-1")

            def eventsData = getInputData(eventsUri)
            def eventsRawJson = new JsonSlurper().parse(eventsData, "ISO-8859-1")

            Conference conference = createConference(conferenceId, conferenceRawJson, eventsRawJson)
            try {
                Map<String, Object> mergedData = new HashMap<>()
                mergedData.put("conferenceData", conferenceRawJson)
                mergedData.put("eventsData", eventsRawJson)
                File backupFile = new File(backupUri)
                if(!backupFile.getParentFile().exists()) {
                    backupFile.getParentFile().mkdirs()
                }
                backupFile.write(JsonOutput.toJson(mergedData), BACKUP_CHARSET)
            } catch (IOException e) {
                log.warn("unable to write backup file '{}'", backupUri, e)
            }
            backupActive = false
            staleException = null
            return conference
        } catch (RuntimeException e) {
            log.error("unable to read data", e)
            staleException = e
            throw e
        }
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    public Conference readConferenceDataFallback(String conferenceNameAndKey) {
        try {
            String[] parts = conferenceNameAndKey.split("@")
            String conferenceId = parts[0]
            String conferenceApiKey = parts[1]
            Map<String, String> conferenceConfig = new HashMap<>()
            conferenceConfig.put("conferenceId", conferenceId)
            conferenceConfig.put("conferenceApiKey", conferenceApiKey)
            String backupUri = StrSubstitutor.replace(backupPattern, conferenceConfig, "&{", "}")

            log.info("Rereading data from backup '{}' for conference '{}", backupPattern, conferenceId)
            def rawJson = new JsonSlurper().parse(new File(backupUri).newInputStream(), BACKUP_CHARSET)
            def conferenceRawJson = rawJson.conferenceData
            def eventsRawJson = rawJson.eventsData
            Conference conference = createConference(conferenceId, conferenceRawJson, eventsRawJson)
            backupActive = true
            return conference
        } catch (RuntimeException e) {
            log.error("unable to read backup", e);
            throw e
        }
    }


    private Object getInputData(String uri) {
        uri.startsWith("resource:") ? readResource(uri) : readSchedFile(uri)
    }

    private InputStream readResource(String uri) {
        log.info("Reading JSON data from local file")
        String[] resourceParts = uri.split(":")
        InputStream stream = this.getClass().getResourceAsStream(resourceParts[1])
        if(stream == null) {
            throw new IOException("file '" + uri + "' not found")
        }
        return stream;
    }

    private URL readSchedFile(String uri) {
        log.info("Reading JSON data from remote '{}'", uri)
        return new URL(uri)
    }

    Conference createConference(String conferenceId, conferenceJson, eventsJson) {
        SchedDataExtractor extractor = new SchedDataExtractor(
                conferenceJson: conferenceJson, eventsJson: eventsJson, conferenceId: conferenceId)
        return extractor.getConference()
    }
}
