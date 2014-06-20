package de.eleon.jswap;

import com.google.common.collect.ImmutableList;
import com.jayway.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static de.eleon.jswap.test.Util.dropCreate;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.createFile;

public class JSwapThreadTest {

    Path baseFrom;
    Path baseTo;
    Path baseFromB;
    Path baseToB;


    @Before
    public void setUp() throws IOException {
        baseFrom = dropCreate("/tmp/from");
        baseTo = dropCreate("/tmp/to");
        baseFromB = dropCreate("/tmp/fromB");
        baseToB = dropCreate("/tmp/toB");

        Awaitility.reset();
    }

    @Test
    public void shouldCopyFile() throws Exception {
        JSwap jSwap = new JSwap(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());
        jSwap.run(true);
        createFile(Paths.get(baseFrom.toString() + "/test.txt"));

        await("copy process")
                .until(fileExists(Paths.get(baseTo.toString() + "/test.txt")));
    }

    @Test
    public void shouldInitializeAgain() throws Exception {
        JSwap jSwap = new JSwap(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());
        jSwap.run(true);

        baseFrom = dropCreate("/tmp/from");
        baseTo = dropCreate("/tmp/to");

        createFile(Paths.get(baseFrom.toString() + "/test.txt"));

        await("copy process")
                .until(fileExists(Paths.get(baseTo.toString() + "/test.txt")));
    }

    @Test
    public void shouldCopyFileFromNewSubdir() throws Exception {
        JSwap jSwap = new JSwap(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());
        jSwap.run(true);

        createDirectory(Paths.get(baseFrom.toString() + "/dir"));
        createFile(Paths.get(baseFrom.toString() + "/dir/test.txt"));

        await("copy process")
                .until(fileExists(Paths.get(baseTo.toString() + "/dir/test.txt")));
    }

    private Callable<Boolean> fileExists(final Path path) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return Files.exists(path);
            }
        };
    }

}
