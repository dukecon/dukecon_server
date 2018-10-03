package org.dukecon.server.repositories

import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.server.conference.ConferencesConfiguration

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Supplier

/**
 * Takes input resources from configuration as single String or Map with multiple Strings and wraps resource strings in
 * @{@link ResourceWrapper} as map values. In case of a single resource string the only map key will be 'eventsData'.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class RawDataResources implements Supplier<Map<String, ResourceWrapper>> {

    final Map<String, ResourceWrapper> resources

    private RawDataResources(Map<String, ResourceWrapper> resources) {
        this.resources = resources
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    static RawDataResources of(ConferencesConfiguration.Conference config, String backupDir = 'tempbackupdir') {
        new RawDataResources(resourcesOf(config.talksUri, backupDir, config.id))
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    static RawDataResources of(talksUri, String conferenceId = 'javaland', String backupDir = 'tempbackupdir') {
        new RawDataResources(resourcesOf(talksUri, backupDir, conferenceId))
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private static Map<String, ResourceWrapper> resourcesOf(Map<String, String> fileUrls, String backupDir, String conferenceId, String type = "eventsData") {
        fileUrls.collectEntries { String type1, String url -> resourcesOf(url, backupDir, conferenceId, type1) }
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private static Map<String, ResourceWrapper> resourcesOf(String fileUrl, String backupDir, String conferenceId, String type = "eventsData") {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
        def dir = "${backupDir}/${conferenceId}/${now}"
        File file
        try {
            file = FileBackuper.of(fileUrl.toURL(), dir, "${type}.json")
        } catch (e) {
        }
        [(type): ResourceWrapper.of(file ?: fileUrl)]

    }

    @Override
    Map<String, ResourceWrapper> get() {
        return resources
    }
}
