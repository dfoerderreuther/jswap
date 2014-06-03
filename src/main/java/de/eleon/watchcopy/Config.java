package de.eleon.watchcopy;

import com.google.common.base.Objects;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {

    private Path from;
    private Path to;

    public Config(String from, String to, String ignore) {
        this.from = Paths.get(from);
        this.to = Paths.get(to);
    }

    public boolean isResponsible(String changed) {
        return changed.startsWith(from.toString());
    }

    public Path getFrom() {
        return from;
    }

    public Path getTo() {
        return to;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(Config.class)
                .add("from", from.toString())
                .add("to", to.toString())
                .toString();
    }
}
