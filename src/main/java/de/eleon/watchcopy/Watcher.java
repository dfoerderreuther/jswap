package de.eleon.watchcopy;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static de.eleon.watchcopy.Log.ERROR;
import static de.eleon.watchcopy.Log.LOG;
import static java.nio.file.StandardWatchEventKinds.*;

public class Watcher {

    private final WatchService watchService;
    private final WatchEventProcessor watchEventProcessor;

    private final Path baseFrom;

    public Watcher(String baseFrom, String baseTo) throws IOException {
        LOG("START: from %s to %s", baseFrom, baseTo);
        this.baseFrom = Paths.get(baseFrom);
        this.watchService = FileSystems.getDefault().newWatchService();
        this.watchEventProcessor = new WatchEventProcessor(this.baseFrom, Paths.get(baseTo));

        registerAll(this.baseFrom);

    }

    public void run(final boolean daemon) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(daemon);
                return t;
            }
        });
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                watch();
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }


    public void watch()  {
        try {
            WatchKey watchKey =watchService.poll(60, TimeUnit.HOURS);
            List<WatchEvent<?>> events = watchKey.pollEvents();
            for (WatchEvent event : events) {
                this.watchEventProcessor.process(watchKey, event);

                processNewDirectories(watchKey, event);
            }
            if (!watchKey.reset()) {
                LOG("watch key no longer valid");
            }
        } catch (InterruptedException e) {
            ERROR(e, "interrupted");
        }
    }

    private void registerAll(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void register(Path path) throws IOException {
        path.register(
                watchService,
                ENTRY_CREATE,
                ENTRY_DELETE,
                ENTRY_MODIFY
        );
    }

    private void processNewDirectories(WatchKey watchKey, WatchEvent event) {
        if (event.kind().equals(ENTRY_CREATE)) {
            Path createdEntry = Paths.get(((Path) watchKey.watchable()).toString() + "/" + ((Path) event.context()).toString());
            if (Files.isDirectory(createdEntry)) {
                try {
                    LOG("register new directory %s", createdEntry);
                    register(createdEntry);
                } catch (IOException e) {
                    ERROR("ERROR, cant register new directory %s, %s", createdEntry, e.toString());
                }
            }
        }
    }

}
