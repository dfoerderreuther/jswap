package de.eleon.watchcopy;

import java.io.IOException;
import java.nio.file.*;

import static de.eleon.watchcopy.Log.LOG;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Handles all change events and synchronize the changings to the target directory
 */
class WatchEventProcessor {

    /**
     * Constructor
     *
     */
    public WatchEventProcessor() {
    }

    /**
     * Process a change event
     *  @param watchKey WatchKey which has detected the change
     * @param event the published WatchEvent
     * @param config
     */
    public void process(WatchKey watchKey, WatchEvent event, Config config) {
        WatchEvent.Kind eventKind = event.kind();
        Path watchedPath = (Path)watchKey.watchable();
        Path target = (Path)event.context();

        String relativePath = watchedPath.toString().substring(config.getFrom().toString().length()) + "/" + target;

        if (eventKind.equals(ENTRY_CREATE)) {
            copy(relativePath, config);

        } else if (eventKind.equals(ENTRY_DELETE)) {
            delete(relativePath, config);

        } else if (eventKind.equals(ENTRY_MODIFY)) {
            copy(relativePath, config);

        }
    }

    private void delete(String relativePath, Config config)  {
        Path delete = toPath(config.getTo(), relativePath);
        LOG("delete: %s", delete);
        try {
            Files.delete(delete);
        } catch (IOException e) {
            Log.ERROR("ERROR, process delete: %s", e);
        }
    }

    private void copy(String relativePath, Config config)  {
        Path from = toPath(config.getFrom(), relativePath);
        Path to = toPath(config.getTo(), relativePath);
        LOG("copy: %s > %s", from, to);
        try {
            Files.copy(
                    from,
                    to,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            Log.ERROR("ERROR, process copy: %s", e);
        }
    }

    private static Path toPath(Path base, String relative) {
        return Paths.get(base.toString() + "/" + relative);
    }
}
