package de.eleon.watchcopy.agent;

import de.eleon.watchcopy.Log;
import de.eleon.watchcopy.WatchCopy;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

import static de.eleon.watchcopy.Log.ERROR;

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
        String from = System.getProperty("watchcopy.from");
        String to = System.getProperty("watchcopy.to");
        if (from == null || from.isEmpty() || to == null || to.isEmpty()) {
            throw new UnsupportedOperationException("Java was not started with -Dwatchcopy.from=<yourMavenProject>/target/classes and -Dwatchcopy.to=${SERVER}/webapps/ROOT/WEB-INF/classes");
        }
        try {
            watchCopy = new WatchCopy(from, to);
            watchCopy.run(true);
        } catch (IOException e) {
            ERROR(e, "error");
        }
    }

    public static Instrumentation getInstrumentation() {
        if (instrumentation == null) {
            throw new UnsupportedOperationException("Java was not started with preMain -javaagent for WatchCopy");
        }
        return instrumentation;
    }
}
