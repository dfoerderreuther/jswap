package de.eleon.watchcopy.agent;

import de.eleon.watchcopy   .Log;
import de.eleon.watchcopy.WatchCopy;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

import static de.eleon.watchcopy.Log.ERROR;

/**
 * JVM Agent to start WatchCopy inside a running JVM
 */
public class WatchCopyAgent {

    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) throws Exception {
        if (instrumentation != null) {
            return;
        }
        instrumentation = inst;
        start();
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        if (instrumentation != null) {
            return;
        }
        instrumentation = inst;
        start();
    }

    private static void start() {
        Log.LOG("init");
        String from = System.getProperty("watchcopy.from");
        String to = System.getProperty("watchcopy.to");
        if (from == null || to == null) {
            throw new UnsupportedOperationException("Java was not started with -Dwatchcopy.from=<yourMavenProject>/target/classes and -Dwatchcopy.to=${SERVER}/webapps/ROOT/WEB-INF/classes");
        }
        try {
            WatchCopy watchCopy = new WatchCopy(from, to);
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
