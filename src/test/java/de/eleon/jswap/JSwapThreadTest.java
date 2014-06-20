package de.eleon.jswap;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static de.eleon.jswap.test.Util.dropCreate;
import static org.junit.Assert.assertTrue;

public class JSwapThreadTest {

    Path baseFrom;
    Path baseTo;
    Path baseFromB;
    Path baseToB;

    CountDownLatch latch;

    @Before
    public void setUp() throws IOException {
        System.out.println("setUp");
        baseFrom = dropCreate("/tmp/from");
        baseTo = dropCreate("/tmp/to");
        baseFromB = dropCreate("/tmp/fromB");
        baseToB = dropCreate("/tmp/toB");

        latch = new CountDownLatch(1);
    }

    @Test
    public void shouldCopyFile() throws Exception {
        JSwap jSwap = new JSwap(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());
        jSwap.run(true);
        Files.createFile(Paths.get(baseFrom.toString() + "/test.txt"));
        latch.await(1, TimeUnit.SECONDS);
        assertTrue(Files.exists(Paths.get(baseTo.toString() + "/test.txt")));
    }

    @Test
    public void shouldInitializeAgain() throws Exception {
        JSwap jSwap = new JSwap(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());
        jSwap.run(true);

        baseFrom = dropCreate("/tmp/from");
        baseTo = dropCreate("/tmp/to");

        Files.createFile(Paths.get(baseFrom.toString() + "/test.txt"));
        latch.await(3, TimeUnit.SECONDS);
        assertTrue(Files.exists(Paths.get(baseTo.toString() + "/test.txt")));
    }

    @Test
    public void shouldCopyFileFromNewSubdir() throws Exception {
        JSwap jSwap = new JSwap(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());

        Files.createDirectory(Paths.get(baseFrom.toString() + "/dir"));

        Files.createFile(Paths.get(baseFrom.toString() + "/dir/test.txt"));
        latch.await(1, TimeUnit.SECONDS);

        assertTrue(Files.exists(Paths.get(baseTo.toString() + "/dir/test.txt")));
    }

}
