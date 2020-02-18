package org.dukecon.server.util

import org.apache.commons.lang3.StringUtils

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class ResourcesFinder {

    final File folder

    ResourcesFinder(String folder) {
        this.folder = new File(folder)
    }

    Optional<Map<String, File>> getFileList() {
        if (!folder.exists()) {
            return Optional.empty()
        }
        def files = folder.listFiles() as List
        Optional.of(files.collectEntries {file -> [(StringUtils.substringBefore(file.name, ".")): file]})
    }

    String getCategory() {
        folder.name
    }
}
