package de.eleon.watchcopy.agent;

import de.eleon.watchcopy.Config;
import de.eleon.watchcopy.Configs;
import de.eleon.watchcopy.Log;
import de.eleon.watchcopy.WatchCopy;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * JVM Agent to init WatchCopy inside a running JVM
 */
public class WatchCopyAgent {

    protected static Instrumentation instrumentation;
    protected static WatchCopy watchCopy;

    public static void premain(String args, Instrumentation instrumentation) throws Exception {
        agentmain(args, instrumentation);
    }

    public static void agentmain(String args, Instrumentation instrumentation) throws Exception {
        if (WatchCopyAgent.instrumentation != null) {
            return;
        }
        WatchCopyAgent.instrumentation = instrumentation;

        startWatchCopy();
    }

    private static void startWatchCopy() {
        Log.LOG("init");
        try {
            List<Config> configs = Configs.getConfigsFromSystemProperties("watchcopy");
            watchCopy = new WatchCopy(configs);
            watchCopy.run(true);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("\nJVM was not started with \n" +
                    "<pathTo>/watchcopy-agent.jar \\\n" +
                    "\t -Dwatchcopy.from[0]=<yourBuildDirectory> \\\n" +
                    "\t -Dwatchcopy.to[0]=<yourClasspathDirectory>\n", e);
        } catch (IOException e) {
            throw new UnsupportedOperationException("\nJVM was not started with \n" +
                    "<pathTo>/watchcopy-agent.jar \\\n" +
                    "\t -Dwatchcopy.from[0]=<yourBuildDirectory> \\\n" +
                    "\t -Dwatchcopy.to[0]=<yourClasspathDirectory>\n", e);
        }
    }

    public static Instrumentation getInstrumentation() {
        if (instrumentation == null) {
            throw new UnsupportedOperationException("Java was not started with preMain -javaagent for WatchCopy");
        }
        return instrumentation;
    }
}
