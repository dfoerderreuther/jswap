package de.eleon.watchcopy;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Log.LOG("init");
        String from = System.getProperty("watchcopy.from");
        String to = System.getProperty("watchcopy.to");
        try {
            Watcher watcher = new Watcher(from, to);
            watcher.run(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
