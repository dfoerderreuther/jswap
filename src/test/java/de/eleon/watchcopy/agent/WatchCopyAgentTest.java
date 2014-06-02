package de.eleon.watchcopy.agent;

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
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WatchCopyAgentTest extends TestCase {

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
        WatchCopyAgent.instrumentation = null;
        WatchCopyAgent.watchCopy = null;
        System.setProperty("watchcopy.from", "");
        System.setProperty("watchcopy.to", "");
    }

    @Test
    public void shouldNotStartWithoutParams() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        WatchCopyAgent.premain("", instrumentation);
    }

    @Test
    public void shouldStartWithPremain() throws Exception {
        System.setProperty("watchcopy.from", baseFrom.toString());
        System.setProperty("watchcopy.to", baseTo.toString());

        WatchCopyAgent.premain("", instrumentation);
        assertTrue(WatchCopyAgent.watchCopy.active());

        WatchCopyAgent.watchCopy.stop();
    }

    @Test
    public void shouldStartWithAgentmain() throws Exception {
        System.setProperty("watchcopy.from", baseFrom.toString());
        System.setProperty("watchcopy.to", baseTo.toString());

        WatchCopyAgent.agentmain("", instrumentation);
        assertTrue(WatchCopyAgent.watchCopy.active());

        WatchCopyAgent.watchCopy.stop();
    }

    @Test
    public void shouldSetInstrumentation() throws Exception {
        System.setProperty("watchcopy.from", baseFrom.toString());
        System.setProperty("watchcopy.to", baseTo.toString());

        WatchCopyAgent.agentmain("", instrumentation);
        assertThat(WatchCopyAgent.getInstrumentation(), is(instrumentation));
    }

}