package org.dukecon.server.conference

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets

/**
 * Accessing remote resources and applying circuit breaker as needed.
 *
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 * @author Falk Sippach, falk@jug-da.de, @sippsack

 * @author Alexander Schwartz, alexander.schwartz@gmx.net, @ahus1de
 *         (added Hystrix)
 */
@Slf4j
@Component
@TypeChecked
class JavalandDataRemote {

    @Value("\${talks.uri:https://www.javaland.eu/api/schedule/JavaLand2016/jl.php?key=TestJL}")
    String talksUri

    @Value("\${talks.backup:javaland-2016-backup.raw}")
    String backup

    @Value("\${conference.url:http://dukecon.org}")
    String conferenceUrl

    @Value("\${conference.name:DukeCon Conference}")
    String conferenceName

    volatile boolean backupActive = false;

    Exception staleException;

    /* JsonOutput and JsonSlurper will encode/parse UTF characters as \\u anyway,
       but to be sure choosing UTF-8 here.
     */
    private final static String BACKUP_CHARSET = StandardCharsets.UTF_8.toString();

    @TypeChecked(TypeCheckingMode.SKIP)
    @HystrixCommand(groupKey = "doag", commandKey = "readConferenceData", fallbackMethod = "readConferenceDataFallback")
    public Conference readConferenceData() {
        try {
            log.info("Rereading data from '{}'", talksUri)
            def data = getInputData(talksUri);
            def rawJson = new JsonSlurper().parse(data, "ISO-8859-1")
            Conference conference = createConference(rawJson.hits.hits._source)
            try {
                File backupFile = new File(backup);
                backupFile.write(JsonOutput.toJson(rawJson), BACKUP_CHARSET);
            } catch (IOException e) {
                log.warn("unable to write backup file '{}'", backup, e);
            }
            backupActive = false;
            staleException = null;
            return conference;
        } catch (RuntimeException e) {
            log.error("unable to read data", e);
            staleException = e;
            throw e;
        }
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    public Conference readConferenceDataFallback() {
        try {
            log.info("Rereading data from backup '{}'", backup)
            def rawJson = new JsonSlurper().parse(new File(backup).newInputStream(), BACKUP_CHARSET)
            Conference conference = createConference(rawJson.hits.hits._source)
            backupActive = true;
            return conference;
        } catch (RuntimeException e) {
            log.error("unable to read backup", e);
            throw e;
        }
    }


    private Object getInputData(String uri) {
        uri.startsWith("resource:") ? readResource(uri) : readJavalandFile(uri)
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

    private URL readJavalandFile(String uri) {
        log.info("Reading JSON data from remote '{}'", uri)
        return new URL(uri)
    }

    Conference createConference(rawJson) {
        JavalandDataExtractor extractor = new JavalandDataExtractor(talksJson: rawJson, conferenceName: conferenceName, conferenceUrl: conferenceUrl)
        return extractor.buildConference()
    }
}
