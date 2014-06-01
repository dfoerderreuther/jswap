package de.eleon.watchcopy.agent;

import de.eleon.watchcopy.Log;
import de.eleon.watchcopy.Watcher;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static de.eleon.watchcopy.Log.ERROR;

public class WatchCopyTransformer implements ClassFileTransformer {

    public WatchCopyTransformer() {
        Log.LOG("init");
        String from = System.getProperty("watchcopy.from");
        String to = System.getProperty("watchcopy.to");

        try {
            Watcher watcher = new Watcher(from, to);
            watcher.run(true);
        } catch (IOException e) {
            ERROR(e, "error");
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return new byte[0];
    }

}
