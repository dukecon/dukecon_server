package org.dukecon.server.adapter

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import groovy.json.JsonOutput
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.model.Conference
import org.dukecon.server.conference.ConferencesConfiguration

import java.nio.charset.StandardCharsets

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Slf4j
@TypeChecked
class WebResourceDataProviderRemote {

    // JsonOutput and JsonSlurper will encode/parse UTF characters as \\u anyway,
    // but to be sure choosing UTF-8 here.
    private final static String BACKUP_CHARSET = StandardCharsets.UTF_8.toString();

    private final ConferenceDataExtractor extractor
    private final ConferencesConfiguration.Conference config

    volatile boolean backupActive = false
    Exception staleException
    volatile Conference conference

    WebResourceDataProviderRemote(ConferencesConfiguration.Conference config, ConferenceDataExtractor extractor) {
        this.extractor = extractor
        this.config = config
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    // @HystrixCommand(groupKey = "doag", commandKey = "readConferenceData", fallbackMethod = "readConferenceDataFallback")
    public Conference readConferenceData() {
        try {
            log.info("Rereading data from '{}'", config.talksUri)
            Conference conference = extractor.getConference()
            try {
                File backupFile = new File(config.backupUri)
                backupFile.write(JsonOutput.toJson(extractor.rawDataMapper.asMap()), BACKUP_CHARSET)
            } catch (IOException e) {
                log.error("unable to write backup file '{}': {}", config.backupUri, e.message, e)
            }
            backupActive = false
            staleException = null
            return conference;
        } catch (RuntimeException e) {
            // TODO: Either log an error or re-throw it!
            log.error("unable to read data: {}", e.message, e)
            staleException = e
            throw e
        }
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    public Conference readConferenceDataFallback() {
        try {
            log.info("Rereading JSON data from backup '{}'", config.backupUri)
            extractor.rawDataMapper.useBackup(ResourceWrapper.of("file:${this.config.backupUri}"))
            Conference conference = extractor.getConference()
            backupActive = true
            return conference
        } catch (RuntimeException e) {
            log.error("unable to read backup", e);
            backupActive = false
            throw e;
        }
    }
}
