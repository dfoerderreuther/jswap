/*
* Copyright 2014 Dominik Foerderreuther <dominik@eleon.de>
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
    public void shouldCopyFileOnStart() throws IOException {
        Path testFile = Paths.get(baseFrom.toString(), "/com/example/Test.class");
        Path resultFile = Paths.get(baseTo.toString(), "/com/example/Test.class");
        Files.createDirectories(testFile.getParent());
        Files.createFile(testFile);

        WatchCopy watchCopy = new WatchCopy(ImmutableList.<Config>builder().add(new Config(baseFrom.toString(), baseTo.toString(), "")).build());


        assertTrue(String.format("directory \"%s\" should be created", resultFile.getParent().toString()), resultFile.getParent().toFile().exists());
        assertTrue(String.format("file \"%s\" should be created", resultFile.toString()), resultFile.toFile().exists());
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
