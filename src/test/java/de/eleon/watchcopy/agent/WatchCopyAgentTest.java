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
        System.setProperty("watchcopy.from[0]", "");
        System.setProperty("watchcopy.to[0]", "");
    }

    @Test
    public void shouldNotStartWithoutParams() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        WatchCopyAgent.premain("", instrumentation);
    }

    @Test
    public void shouldStartWithPremain() throws Exception {
        System.setProperty("watchcopy.from[0]", baseFrom.toString());
        System.setProperty("watchcopy.to[0]", baseTo.toString());

        WatchCopyAgent.premain("", instrumentation);
        assertTrue(WatchCopyAgent.watchCopy.active());

        WatchCopyAgent.watchCopy.stop();
    }

    @Test
    public void shouldStartWithAgentmain() throws Exception {
        System.setProperty("watchcopy.from[0]", baseFrom.toString());
        System.setProperty("watchcopy.to[0]", baseTo.toString());

        WatchCopyAgent.agentmain("", instrumentation);
        assertTrue(WatchCopyAgent.watchCopy.active());

        WatchCopyAgent.watchCopy.stop();
    }

    @Test
    public void shouldSetInstrumentation() throws Exception {
        System.setProperty("watchcopy.from[0]", baseFrom.toString());
        System.setProperty("watchcopy.to[0]", baseTo.toString());

        WatchCopyAgent.agentmain("", instrumentation);
        assertThat(WatchCopyAgent.getInstrumentation(), is(instrumentation));
    }

}