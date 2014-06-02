package de.eleon.watchcopy;

import java.io.IOException;

/**
 * Main class to start WatchCopy from commandline.
 *
 * Usage:     java -jar <pathTo>/watchcopy-agent.jar \
 *                  -Dwatchcopy.from=<yourMavenProject>/target/classes \
 *                  -Dwatchcopy.to=${SERVER}/webapps/ROOT/WEB-INF/classes
 */
public class Main {

    protected static WatchCopy watchCopy;

    public static void main(String[] args) {
        Log.LOG("init");
        String from = System.getProperty("watchcopy.from");
        String to = System.getProperty("watchcopy.to");
        if (from == null || from.isEmpty() || to == null || to.isEmpty()) {
            throw new UnsupportedOperationException("Usage: \n" +
                    "java -jar <pathTo>/watchcopy-agent.jar \\\n" +
                    "\t -Dwatchcopy.from=<yourMavenProject>/target/classes \\\n" +
                    "\t -Dwatchcopy.to=${SERVER}/webapps/ROOT/WEB-INF/classes");
        }
        try {
            watchCopy = new WatchCopy(from, to);
            watchCopy.run(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
