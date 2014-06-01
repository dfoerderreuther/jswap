package de.eleon.watchcopy.agent;

import java.lang.instrument.Instrumentation;

public class WatchCopyAgent {

    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) throws Exception {
        if (instrumentation != null) {
            return;
        }
        instrumentation = inst;
        instrumentation.addTransformer(new WatchCopyTransformer());
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        if (instrumentation != null) {
            return;
        }
        instrumentation = inst;
        instrumentation.addTransformer(new WatchCopyTransformer());
    }

    public static Instrumentation getInstrumentation() {
        if (instrumentation == null) {
            throw new UnsupportedOperationException("Java was not started with preMain -javaagent for WatchCopy");
        }
        return instrumentation;
    }
}
