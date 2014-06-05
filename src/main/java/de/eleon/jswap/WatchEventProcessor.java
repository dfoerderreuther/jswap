/*
* Copyright 2014 Dominik Foerderreuther <dominik@eleon.de>
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package de.eleon.jswap;

import java.io.IOException;
import java.nio.file.*;

import static de.eleon.jswap.Log.LOG;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Handles all change events and synchronize the changings to the target directory
 */
class WatchEventProcessor {

    public WatchEventProcessor() { }

    /**
     * Process a change event
     * @param watchKey WatchKey which has detected the change
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

    private void delete(String relativePath, Config config) {
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
            Files.createDirectories(to.getParent());
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
