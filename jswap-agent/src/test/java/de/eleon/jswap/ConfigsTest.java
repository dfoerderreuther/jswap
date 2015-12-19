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
package de.eleon.jswap;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

public class ConfigsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldGetConfig() {
        Properties properties = new Properties();
        properties.setProperty("jswap.from[0]", "/tmp/from1");
        properties.setProperty("jswap.to[0]", "/tmp/to1");

        properties.setProperty("jswap.from[1]", "/tmp/from2");
        properties.setProperty("jswap.to[1]", "/tmp/to2");

        List<Config> configs = Configs.getConfigsFromPoperties("jswap", properties);

        assertThat(configs.size(), is(2));

        Optional<Config> config0 = byFrom(configs, "/tmp/from1");
        assertTrue(config0.isPresent());
        assertThat(config0.get().getTo().toString(), is("/tmp/to1"));


        Optional<Config> config1 = byFrom(configs, "/tmp/from2");
        assertTrue(config1.isPresent());
        assertThat(config1.get().getTo().toString(), is("/tmp/to2"));

    }

    @Test
    public void shouldGetErrorWhenEmpty() {
        Properties properties = new Properties();

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("need params -Djswap.from[0]=value -Djswap.to[0]=value");
        List<Config> configs = Configs.getConfigsFromPoperties("jswap", properties);
    }

    @Test
    public void shouldGetErrorWhenIncomplete() {
        Properties properties = new Properties();
        properties.setProperty("jswap.from[0]", "/tmp/from1");

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("need param -Djswap.to[0]=value");
        List<Config> configs = Configs.getConfigsFromPoperties("jswap", properties);
    }

    @Test
    public void shouldGetErrorWhenParamEmpty() {
        Properties properties = new Properties();
        properties.setProperty("jswap.from[0]", "");
        properties.setProperty("jswap.from[1]", "");

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("need param -Djswap.from[0]=value");
        List<Config> configs = Configs.getConfigsFromPoperties("jswap", properties);
    }

    @Test
    public void shouldGetFromSystemProperties() {
        System.setProperty("jswap.from[0]", "/tmp/sys/from1");
        System.setProperty("jswap.to[0]", "/tmp/sys/to1");

        List<Config> configs = Configs.getConfigsFromSystemProperties("jswap");

        Optional<Config> config0 = byFrom(configs, "/tmp/sys/from1");
        assertTrue(config0.isPresent());
        assertThat(config0.get().getTo().toString(), is("/tmp/sys/to1"));
    }

    private Optional<Config> byFrom(List<Config> configs, final String from) {
        return FluentIterable.from(configs).firstMatch(new Predicate<Config>() {
            public boolean apply(Config config) {
                return config.getFrom().toString().equals(from);
            }
        });
    }

}