package org.dukecon.server.services;

import org.dukecon.server.conference.ConferencesConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Service
public class FileWatcherService implements Runnable {
    private final Logger log = LoggerFactory.getLogger(FileWatcherService.class);

    private final WatchService watcher;
    private final ConferencesConfigurationService service;

    public FileWatcherService(ConferencesConfigurationService service, @Value("${cache.dir:cache}") Path cacheDir) throws IOException {
        this.service = service;
        this.watcher = getDefault().newWatchService();
        registerDirectoryAndSubDirectories(cacheDir);
    }

    private void registerDirectoryAndSubDirectories(final Path start) throws IOException {
        if (!Files.isDirectory(start)) {
            log.info("Directory does not exist: " + start.toAbsolutePath());
            return;
        }
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                log.info("Register file watcher for " + dir.toAbsolutePath());
                dir.register(watcher, ENTRY_MODIFY);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void run() {
        WatchKey key = null;
        while (true) {
            try {
                if (!((key = watcher.take()) != null)) break;
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new IllegalStateException("File watching failed", e);
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                Path fullPath = ((Path) key.watchable()).resolve((Path) event.context());
                log.info(String.format("File changed: %s, will be reloaded.", fullPath));
                service.reloadInputFile(fullPath);
            }
            key.reset();
        }
    }

}
