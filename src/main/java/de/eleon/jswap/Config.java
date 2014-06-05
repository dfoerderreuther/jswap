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

import com.google.common.base.Objects;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {

    private final Path from;
    private final Path to;

    public Config(String from, String to, String ignore) {
        this.from = Paths.get(from);
        this.to = Paths.get(to);
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
