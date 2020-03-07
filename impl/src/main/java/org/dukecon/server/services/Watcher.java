package org.dukecon.server.services;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
public class Watcher {
    public static void main(String[] args) throws IOException, InterruptedException {
        WatchService watchService
                = FileSystems.getDefault().newWatchService();

        Path path = Paths.get(System.getProperty("user.home") + "/falk");

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                dir.register(watchService, ENTRY_MODIFY);
                return FileVisitResult.CONTINUE;
            }

        });

//        path.register(
//                watchService,
//                ENTRY_MODIFY);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(
                        "Event kind:" + event.kind()
                                + ". File affected: " + event.context() + ".");
                key.reset();
            }
        }
    }
}
