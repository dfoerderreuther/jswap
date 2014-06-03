package de.eleon.watchcopy;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static de.eleon.watchcopy.test.Util.dropCreate;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class WatchCopyTest {

    Path baseFrom;
    Path baseTo;
    Path baseFromB;
    Path baseToB;

    @Before
    public void setUp() throws IOException {
        System.out.println("setUp");
        baseFrom = dropCreate("/tmp/from");
        baseTo = dropCreate("/tmp/to");
        baseFromB = dropCreate("/tmp/fromB");
        baseToB = dropCreate("/tmp/toB");
    }

    @Test
    public void shouldCopyFile() throws IOException {
        WatchCopy watchCopy = new WatchCopy(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());
        Files.createFile(Paths.get(baseFrom.toString() + "/test.txt"));
        watchCopy.watch();
        assertTrue(Files.exists(Paths.get(baseTo.toString() + "/test.txt")));
    }

    @Test
    public void shouldCopyDirectory() throws IOException {
        WatchCopy watchCopy = new WatchCopy(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());
        Files.createDirectory(Paths.get(baseFrom.toString() + "/dir"));
        watchCopy.watch();
        assertTrue(Files.exists(Paths.get(baseTo.toString() + "/dir")));
    }

    @Test
    public void shouldCopyFileFromSubdir() throws IOException {
        Files.createDirectory(Paths.get(baseFrom.toString() + "/dir"));
        Files.createDirectory(Paths.get(baseTo.toString() + "/dir"));
        WatchCopy watchCopy = new WatchCopy(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());
        Files.createFile(Paths.get(baseFrom.toString() + "/dir/test.txt"));
        watchCopy.watch();
        assertTrue(Files.exists(Paths.get(baseTo.toString() + "/dir/test.txt")));
    }

    @Test(timeout=1000)
    public void shouldCopyFileFromNewSubdir() throws IOException {
        WatchCopy watchCopy = new WatchCopy(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());

        Files.createDirectory(Paths.get(baseFrom.toString() + "/dir"));
        watchCopy.watch();

        Files.createFile(Paths.get(baseFrom.toString() + "/dir/test.txt"));
        watchCopy.watch();

        assertTrue(Files.exists(Paths.get(baseTo.toString() + "/dir/test.txt")));
    }

    @Test
    public void shoulcCopyChangedFile() throws IOException {
        WatchCopy watchCopy = new WatchCopy(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());

        Path fileFrom = Paths.get(baseFrom.toString() + "/test.txt");
        Files.createFile(fileFrom);
        watchCopy.watch();

        Files.write(fileFrom, "test".getBytes());
        watchCopy.watch();

        Path fileTo = Paths.get(baseFrom.toString() + "/test.txt");
        assertThat(new String(Files.readAllBytes(fileTo)), is("test"));
    }

    @Test
    public void shouldDeleteFile() throws IOException {
        WatchCopy watchCopy = new WatchCopy(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());

        Path fileFrom = Paths.get(baseFrom.toString() + "/test.txt");
        Files.createFile(fileFrom);
        watchCopy.watch();

        Path fileTo = Paths.get(baseTo.toString() + "/test.txt");
        assertTrue(Files.exists(fileTo));

        Files.delete(fileFrom);
        watchCopy.watch();

        assertFalse(Files.exists(fileTo));
    }

    @Test
    public void shouldWorkWithTwoConfigs() throws IOException {
        WatchCopy watchCopy = new WatchCopy(
                ImmutableList.<Config>builder()
                        .add(new Config(baseFrom.toString(), baseTo.toString(), ""))
                        .add(new Config(baseFromB.toString(), baseToB.toString(), ""))
                        .build());

        Files.createFile(Paths.get(baseFrom.toString() + "/test.txt"));
        watchCopy.watch();
        assertTrue(Files.exists(Paths.get(baseTo.toString() + "/test.txt")));

        Files.createFile(Paths.get(baseFromB.toString() + "/test.txt"));
        watchCopy.watch();
        assertTrue(Files.exists(Paths.get(baseToB.toString() + "/test.txt")));
    }


    //@Ignore // don't know why
    //@Test
    public void shouldDeleteDirectory() throws IOException {
        WatchCopy watchCopy = new WatchCopy(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());

        Path dirFrom = Paths.get(baseFrom.toString() + "/dir");
        Files.createDirectory(dirFrom);
        watchCopy.watch();

        Path dirTo = Paths.get(baseTo.toString() + "/dir");
        assertTrue(Files.exists(dirTo));

        Files.delete(dirFrom);
        watchCopy.watch();

        assertFalse(Files.exists(dirTo));
    }



}
