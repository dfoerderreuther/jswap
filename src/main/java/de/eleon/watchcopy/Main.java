package de.eleon.watchcopy;

import java.io.IOException;
import java.util.List;

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
        try {
            List<Config> configs = Configs.getConfigsFromSystemProperties("watchcopy");
            watchCopy = new WatchCopy(configs);
            watchCopy.run(false);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("\nUsage: \n" +
                    "java -jar <pathTo>/watchcopy-agent.jar \\\n" +
                    "\t -Dwatchcopy.from[0]=<yourBuildDirectory> \\\n" +
                    "\t -Dwatchcopy.to[0]=<yourClasspathDirectory>\n", e);
        } catch (IOException e) {
            throw new UnsupportedOperationException("\nUsage: \n" +
                    "java -jar <pathTo>/watchcopy-agent.jar \\\n" +
                    "\t -Dwatchcopy.from[0]=<yourBuildDirectory> \\\n" +
                    "\t -Dwatchcopy.to[0]=<yourClasspathDirectory>\n", e);
        }
    }


}
