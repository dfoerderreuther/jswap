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
package de.eleon.jswap.agent;

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

import static de.eleon.jswap.test.Files.dropCreate;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class JSwapAgentTest extends TestCase {

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
        JSwapAgent.instrumentation = null;
        JSwapAgent.jSwap = null;
        System.setProperty("jswap.from[0]", "");
        System.setProperty("jswap.to[0]", "");
    }

    @Test
    public void shouldNotStartWithoutParams() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        JSwapAgent.premain("", instrumentation);
    }

    @Test
    public void shouldStartWithPremain() throws Exception {
        System.setProperty("jswap.from[0]", baseFrom.toString());
        System.setProperty("jswap.to[0]", baseTo.toString());

        JSwapAgent.premain("", instrumentation);
        assertTrue(JSwapAgent.jSwap.active());

        JSwapAgent.jSwap.stop();
    }

    @Test
    public void shouldSetInstrumentation() throws Exception {
        System.setProperty("jswap.from[0]", baseFrom.toString());
        System.setProperty("jswap.to[0]", baseTo.toString());

        JSwapAgent.premain("", instrumentation);
        assertThat(JSwapAgent.getInstrumentation(), is(instrumentation));
    }

}