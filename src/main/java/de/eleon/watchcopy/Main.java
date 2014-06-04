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

import java.io.IOException;
import java.util.List;

/**
 * Main class to start WatchCopy from commandline.
 *
 * Usage:     java -jar <pathTo>/watchcopy-agent.jar \
 *                  -Dwatchcopy.from=<yourMavenProject>/target/classes \
 *                  -Dwatchcopy.to=${SERVER}/webapps/ROOT/WEB-INF/classes
 */
public class Main {

    protected static WatchCopy watchCopy;

    public static void main(String[] args) {
        Log.LOG("init");
        try {
            List<Config> configs = Configs.getConfigsFromSystemProperties("watchcopy");
            watchCopy = new WatchCopy(configs);
            watchCopy.run(false);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("\nUsage: \n" +
                    "java -jar <pathTo>/watchcopy-agent.jar \\\n" +
                    "\t -Dwatchcopy.from[0]=<yourBuildDirectory> \\\n" +
                    "\t -Dwatchcopy.to[0]=<yourClasspathDirectory>\n", e);
        } catch (IOException e) {
            throw new UnsupportedOperationException("\nUsage: \n" +
                    "java -jar <pathTo>/watchcopy-agent.jar \\\n" +
                    "\t -Dwatchcopy.from[0]=<yourBuildDirectory> \\\n" +
                    "\t -Dwatchcopy.to[0]=<yourClasspathDirectory>\n", e);
        }
    }


}
