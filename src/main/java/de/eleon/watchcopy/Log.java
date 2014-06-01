package de.eleon.watchcopy;

import java.util.Date;

/**
 * Dumb logging class
 */
public class Log {

    public static void LOG(String message, Object... args) {
        System.out.printf("WatchCopy - %s: %s\n", new Date(), String.format(message, args));
    }

    public static void ERROR(String message, Object... args) {
        System.err.printf("WatchCopy - %s: %s\n", new Date(), String.format(message, args));
    }

    public static void ERROR(Throwable e, String message, Object... args) {
        System.err.printf("WatchCopy - %s: %s\n%s\n", new Date(), String.format(message, args), e.fillInStackTrace());
    }

}
