package org.dukecon.server.repositories


import groovy.json.JsonOutput
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.model.Conference
import org.dukecon.server.conference.ConferencesConfiguration
import org.springframework.beans.factory.annotation.Value

import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.charset.StandardCharsets

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Slf4j
@TypeChecked
class WebResourceDataProviderRemote {

    @Value("\${backup.dir:backup/raw}")
    private String backupDir

    // JsonOutput and JsonSlurper will encode/parse UTF characters as \\u anyway,
    // but to be sure choosing UTF-8 here.
    private final static String BACKUP_CHARSET = StandardCharsets.UTF_8.toString();

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
            backupInputFiles(config)
            Conference conference = extractor.getConference()
            try {
                checkIfDirExists(backupDir)
                File backupFile = backupFile()
                log.info("Creating backup in '{}'", backupFile)
                backupFile.write(JsonOutput.toJson(extractor.rawDataMapper.asMap()), BACKUP_CHARSET)
            } catch (IOException e) {
                log.error("Unable to write backup file '{}': {}", config.backupUri, e.message, e)
            }
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

    void checkIfDirExists(String backupDir) {
        File backupDirectory = new File(backupDir)
        if (backupDirectory.exists()) {
            if (!backupDirectory.isDirectory()) {
                log.error("Cannot backup to '{}' - it is not a directory", backupDir)
            }
        } else {
            if (!backupDirectory.mkdirs()) {
                log.error("Cannot create backup directory '{}'", backupDir)
            }
        }
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private void backupInputFiles(ConferencesConfiguration.Conference config) {
        backupInputFiles(config.talksUri, config.id)
    }

    private void backupInputFiles(Map<String, String> fileUrls, String name) {
        fileUrls.each { type, url -> backupInputFiles(url, "${name}_${type}".toString()) }
    }

    private void backupInputFiles(String fileUrl, String name) {
        log.info("Try to backup input file from ${fileUrl} (${name})")
        try {
            checkIfDirExists(backupDir)
            URL website = new URL(fileUrl)
            ReadableByteChannel rbc = Channels.newChannel(website.openStream())
            FileOutputStream fos = new FileOutputStream("${backupDir}/${name}.json")
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE)
        } catch (Exception e) {
            log.warn("Could not backup input file ${fileUrl} (${name}) because of ${e.getClass().getName()} (${e.getMessage()})")
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
