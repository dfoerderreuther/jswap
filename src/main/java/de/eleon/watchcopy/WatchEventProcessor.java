package de.eleon.watchcopy;

import java.io.IOException;
import java.nio.file.*;

import static de.eleon.watchcopy.Log.LOG;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Handles all change events and synchronize the changings to the target directory
 */
class WatchEventProcessor {

    private final Path baseFrom;
    private final Path baseTo;

    /**
     * Constructor
     *
     * @param baseFrom Path of base directory
     * @param baseTo Path of target directory
     */
    public WatchEventProcessor(Path baseFrom, Path baseTo) {
        this.baseFrom = baseFrom;
        this.baseTo = baseTo;
    }

    /**
     * Process a change event
     *
     * @param watchKey WatchKey which has detected the change
     * @param event the published WatchEvent
     */
    public void process(WatchKey watchKey, WatchEvent event) {
        WatchEvent.Kind eventKind = event.kind();
        Path watchedPath = (Path)watchKey.watchable();
        Path target = (Path)event.context();

        String relativePath = watchedPath.toString().substring(this.baseFrom.toString().length()) + "/" + target;

        if (eventKind.equals(ENTRY_CREATE)) {
            copy(relativePath);

        } else if (eventKind.equals(ENTRY_DELETE)) {
            delete(relativePath);

        } else if (eventKind.equals(ENTRY_MODIFY)) {
            copy(relativePath);

        }
    }

    private void delete(String relativePath)  {
        Path delete = toPath(this.baseTo, relativePath);
        LOG("delete: %s", delete);
        try {
            Files.delete(delete);
        } catch (IOException e) {
            Log.ERROR("ERROR, process delete: %s", e);
        }
    }

    private void copy(String relativePath)  {
        Path from = toPath(this.baseFrom, relativePath);
        Path to = toPath(this.baseTo, relativePath);
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
