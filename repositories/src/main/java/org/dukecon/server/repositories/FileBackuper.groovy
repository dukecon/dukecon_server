package org.dukecon.server.repositories

import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.dukecon.adapter.ResourceWrapper

import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Slf4j
@TypeChecked
class FileBackuper {
    private final static String BACKUP_CHARSET = StandardCharsets.UTF_8.toString();

    final File file

    @TypeChecked(TypeCheckingMode.SKIP)
    static File of(sourceFile, String destDir, String name) {
        new FileBackuper(sourceFile, destDir, name).file
    }

    private FileBackuper(String content, String destDir, String name) {
        log.info("Backup text (${content.substring(0, Math.min(content.length(), 10))}) to ${destDir}/${name}")
        checkIfDirExists(destDir)
        try {
            file = new File("${destDir}/${name}")
            file.write(content, BACKUP_CHARSET)
        } catch (Exception e) {
            log.warn("Could not backup text (${content.substring(0, Math.min(content.length(), 10))}) to ${destDir}/${name} because of ${e.getClass().getName()} (${e.getMessage()})")
        }
    }

    private FileBackuper(URL url, String destDir, String name) {
        log.info("Backup input file from ${url} to ${destDir}/${name}")
        checkIfDirExists(destDir)
        try {
            ReadableByteChannel rbc = Channels.newChannel(url.openStream())
            file = new File("${destDir}/${name}.json")
            FileOutputStream fos = new FileOutputStream(file)
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE)
        } catch (Exception e) {
            log.warn("Could not backup input file ${url} to ${destDir}/${name} because of ${e.getClass().getName()} (${e.getMessage()})")
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

}
