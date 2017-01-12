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

    private final RawDataMapper rawDataMapper
    private final ConferencesConfiguration.Conference config

    volatile boolean backupActive = false
    Exception staleException
    volatile Conference conference

    WebResourceDataProviderRemote(RawDataMapper rawDataMapper, ConferencesConfiguration.Conference config) {
        this.rawDataMapper = rawDataMapper
        this.config = config
    }

    private Conference createConference(rawData) {
        ConferenceDataExtractor dataExtractor = this.config.extractorClass.newInstance(this.config.id, rawData, config.startDate, config.name, config.url) as ConferenceDataExtractor
        dataExtractor.conference
    }

    @TypeChecked(TypeCheckingMode.SKIP)
//    @HystrixCommand(groupKey = "doag", commandKey = "readConferenceData", fallbackMethod = "readConferenceDataFallback")
    public Conference readConferenceData() {
        try {
            log.info("Rereading data from '{}'", config.talksUri)
            Conference conference = createConference(rawDataMapper)
            try {
                File backupFile = new File(config.backupUri)
                backupFile.write(JsonOutput.toJson(rawDataMapper.asMap()), BACKUP_CHARSET)
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
            rawDataMapper.useBackup(ResourceWrapper.of("file:backup/${this.config.backupUri}"))
            Conference conference = createConference(rawDataMapper)
            backupActive = true;
            return conference;
        } catch (RuntimeException e) {
            log.error("unable to read backup", e);
            throw e;
        }
    }
}
