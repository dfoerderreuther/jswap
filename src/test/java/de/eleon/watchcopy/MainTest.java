package de.eleon.watchcopy;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;

import static de.eleon.watchcopy.test.Util.dropCreate;

@RunWith(MockitoJUnitRunner.class)
public class MainTest extends TestCase {

    @Mock
    Instrumentation instrumentation;

    Path baseFrom ;
    Path baseTo;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws IOException {
        System.out.println("setUp");
        baseFrom = dropCreate("/tmp/from");
        baseTo = dropCreate("/tmp/to");
        System.setProperty("watchcopy.from", "");
        System.setProperty("watchcopy.to", "");
    }

    @Test
    public void shouldNotStartWithoutParams() {
        thrown.expect(UnsupportedOperationException.class);
        Main.main(new String[]{});
    }

    @Test
    public void shouldStartWithMain() throws Exception {
        System.setProperty("watchcopy.from", baseFrom.toString());
        System.setProperty("watchcopy.to", baseTo.toString());

        Main.main(new String[]{});
        assertTrue(Main.watchCopy.active());

        Main.watchCopy.stop();
    }
}