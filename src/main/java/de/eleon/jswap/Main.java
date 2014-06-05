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

import java.io.IOException;
import java.util.List;

/**
 * Main class to start WatchCopy from commandline.
 *
 * Usage:     java -jar <pathTo>/jswap-agent.jar \
 *                  -Djswap.from=<yourMavenProject>/target/classes \
 *                  -Djswap.to=${SERVER}/webapps/ROOT/WEB-INF/classes
 */
public class Main {

    protected static JSwap JSwap;

    public static void main(String[] args) {
        Log.LOG("init");
        try {
            List<Config> configs = Configs.getConfigsFromSystemProperties("jswap");
            JSwap = new JSwap(configs);
            JSwap.run(false);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("\nUsage: \n" +
                    "java -jar <pathTo>/jswap-agent.jar \\\n" +
                    "\t -Djswap.from[0]=<yourBuildDirectory> \\\n" +
                    "\t -Djswap.to[0]=<yourClasspathDirectory>\n", e);
        } catch (IOException e) {
            throw new UnsupportedOperationException("\nUsage: \n" +
                    "java -jar <pathTo>/jswap-agent.jar \\\n" +
                    "\t -Djswap.from[0]=<yourBuildDirectory> \\\n" +
                    "\t -Djswap.to[0]=<yourClasspathDirectory>\n", e);
        }
    }


}
