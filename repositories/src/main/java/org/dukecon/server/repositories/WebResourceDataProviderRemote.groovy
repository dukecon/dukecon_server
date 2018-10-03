package org.dukecon.server.repositories

import groovy.json.JsonOutput
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.model.Conference
import org.dukecon.server.conference.ConferencesConfiguration
import org.springframework.beans.factory.annotation.Value

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Slf4j
@TypeChecked
class WebResourceDataProviderRemote {

    @Value("\${backup.dir:backup/raw}")
    private String backupDir

    private final ConferenceDataExtractor extractor
    private final ConferencesConfiguration.Conference config

    volatile boolean backupActive = false
    Exception staleException = null
    volatile Conference conference

    WebResourceDataProviderRemote(ConferencesConfiguration.Conference config, ConferenceDataExtractor extractor) {
        this.extractor = extractor
        this.config = config
    }

    private File backupFile() {
        File backupDirectory = new File(backupDir)
        return new File(backupDirectory, config.backupUri)
    }

    // @HystrixCommand(groupKey = "doag", commandKey = "readConferenceData", fallbackMethod = "readConferenceDataFallback")
    public Conference readConferenceData() {
        try {
            log.info("Rereading data from '{}'", config.talksUri)
            Conference conference = extractor.getConference()
            FileBackuper.of(JsonOutput.toJson(extractor.rawDataMapper.asMap()), backupDir, config.backupUri)
            backupActive = false
            staleException = null
            return conference;
        } catch (RuntimeException e) {
            // TODO: Either log an error or re-throw it!
            log.error("Unable to read data: {}", e.message, e)
            staleException = e
            throw e
        }
    }

    public Conference readConferenceDataFallback() {
        try {
            File backupFile = backupFile()
            log.info("Rereading JSON data from backup '{}'", backupFile)
            extractor.rawDataMapper.useBackup(ResourceWrapper.of(backupFile))
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
