package de.eleon.jswap;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static de.eleon.jswap.Log.BANNER;
import static de.eleon.jswap.Log.ERROR;
import static de.eleon.jswap.Log.LOG;
import static java.nio.file.Files.*;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Opens a {link WatchService} and watches a directory with its subdirectories for changes. Detected changes will be
 * processed by {link WatchEventProcessor}. New directories will also be registered within the watchservice.
 */
public class JSwap {

    private final List<Config> configs;
    private final WatchService watchService;
    private final WatchEventProcessor watchEventProcessor;
    private ScheduledExecutorService initExecutor;
    private ScheduledExecutorService watchExecutor;

    private final Map<WatchKey, Config> watchKeyConfigMap = Maps.newHashMap();

    private final Map<String, WatchKey> pathWatchkeys = Maps.newHashMap();


    /**
     * Constructor
     *
     * @param configs List of watch configs
     * @throws IOException
     */
    public JSwap(List<Config> configs) throws IOException {
        BANNER();
        LOG("START: configs %s", Joiner.on(", ").join(configs).toString());
        this.configs = configs;
        this.watchService = FileSystems.getDefault().newWatchService();
        this.watchEventProcessor = new WatchEventProcessor();

        init();
    }

    /**
     * Run watch process in an endless thread. Reconfigure every second.
     *
     * @param daemon boolean if threads should be a daemon
     */
    public void run(final boolean daemon) {
        initExecutor = Executors.newSingleThreadScheduledExecutor(getThreadFactory(daemon));
        initExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 1, 1, TimeUnit.SECONDS);

        watchExecutor = Executors.newSingleThreadScheduledExecutor(getThreadFactory(daemon));
        watchExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                watch();
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    private ThreadFactory getThreadFactory(final boolean daemon) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(daemon);
                return t;
            }
        };
    }


    public void init() {
        for (Config config : configs) {
            initialize(config);
        }
    }


    private void initialize(Config config) {
        try {
            registerAll(config);
        } catch (IOException e) {
            Log.ERROR(e, "error during config registration %s", config);
        }
    }

    /**
     * Watch baseFrom for the next change event
     */
    public void watch()  {
        try {
            WatchKey watchKey = watchService.poll(60, TimeUnit.HOURS);
            List<WatchEvent<?>> events = watchKey.pollEvents();
            for (WatchEvent event : events) {
                Config config = watchKeyConfigMap.get(watchKey);
                this.watchEventProcessor.process(watchKey, event, config);

                processNewDirectories(watchKey, event, config);
            }
            if (!watchKey.reset()) {
                watchKeyConfigMap.remove(watchKey);
                LOG("watch key no longer valid");
            }

        } catch (InterruptedException e) {
            ERROR(e, "interrupted");
        }
    }

    /**
     * Walk through directories and register directories and files
     *
     * @param config Config to register
     * @throws IOException
     */
    private void registerAll(final Config config) throws IOException {
        Files.walkFileTree(config.getFrom(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir, config);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                registerFile(file, config);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Register watchkey for this directory
     *
     * @param path Path to directory
     * @param config Config
     * @throws IOException
     */
    private void registerDirectory(Path path, Config config) throws IOException {
        if (pathWatchkeys.containsKey(path.toString()) && pathWatchkeys.get(path.toString()).isValid()) {
            return;
        }
        if (pathWatchkeys.containsKey(path.toString())) {
            pathWatchkeys.remove(path.toString());
        }
        WatchKey watchKey = path.register(
                watchService,
                ENTRY_CREATE,
                ENTRY_DELETE,
                ENTRY_MODIFY
        );
        pathWatchkeys.put(path.toString(), watchKey);
        watchKeyConfigMap.put(watchKey, config);
    }

    /**
     * Initial copy file to classpath to enable hot deployment
     *
     * @param file Path of file
     * @param config Config
     * @throws IOException
     */
    private void registerFile(Path file, Config config) throws IOException {
        Path to = Paths.get(config.getTo().toString(), file.toString().substring(config.getFrom().toString().length()));
        createDirectories(to.getParent());
        if (!exists(to) || !equals(file, to)) {
            copy(file, to, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        }
    }

    public static boolean equals(Path a, Path b) throws IOException {
        return Files.getLastModifiedTime(a).equals(Files.getLastModifiedTime(b));
    }

    /**
     * Register new created directories after initialization phase
     *
     * @param watchKey WatchKey which has found the new directory
     * @param event the releasing WatchEvent
     * @param config Config
     */
    private void processNewDirectories(WatchKey watchKey, WatchEvent event, Config config) {
        if (event.kind().equals(ENTRY_CREATE)) {
            Path createdEntry = Paths.get(((Path) watchKey.watchable()).toString() + "/" + ((Path) event.context()).toString());
            if (isDirectory(createdEntry)) {
                try {
                    LOG("register new directory %s", createdEntry);
                    registerDirectory(createdEntry, config);
                } catch (IOException e) {
                    ERROR("ERROR, cant register new directory %s, %s", createdEntry, e.toString());
                }
            }
        }
    }

    /**
     * ThreadExecutor status
     *
     * @return true if running
     */
    public boolean active() {
        return watchExecutor != null && !watchExecutor.isTerminated();
    }

    /**
     * Stop the ThreadExecutor
     */
    public void stop() {
        if (watchExecutor == null) return;
        watchExecutor.shutdown();
    }
}
