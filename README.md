# Welcome to JSwap

## What is JSwap?

JSwap is a JVM agent for synchronizing your IDEs or Mavens bin directory with the classpath of a running JVM. It uses 
Spring Loaded for reloading changed classes during runtime. 

# Installation

Download the source and build it with maven.

    mvn clean install
    
The agent-jar will be target/jswap-agent.jar
    
# Running 

## Simple example of running with a single source and a single target folder

    java -javaagent:<pathTo>/jswap-agent.jar \
        -Djswap.from[0]=<pathOfYourIDEsBinDirectory> \
        -Djswap.to[0]=<pathOfYourClasspathDirectory> \
        ... SomeJavaClass
        
## Example of running with Tomcat, synchronize classes of two modules and static resources

    SERVER=<pathTo>/server/apache-tomcat

	export JAVA_OPTS="$JAVA_OPTS -Ddev=true \
	        -Dcom.sun.management.jmxremote \
	        -noverify"

	# JSwap
	export JAVA_OPTS="$JAVA_OPTS -javaagent:<pathTo>/jswap-agent.jar \
	        -Djswap.from[0]=<yourMavenProject>/target/classes \
	        -Djswap.to[0]=${SERVER}/webapps/ROOT/WEB-INF/classes \
            -Djswap.from[1]=<anotherModul>/target/classes \
            -Djswap.to[1]=${SERVER}/webapps/ROOT/WEB-INF/classes \
            -Djswap.from[2]=<yourMavenProject>/src/main/webapps \
            -Djswap.to[2]=${SERVER}/webapps/ROOT"

	$SERVER/bin/catalina.sh jpda start
	
## Enable deletion

By default jswap doesn't delete file or directories from target directories, even if the 
sources are missing (e.g. mvn clean). To enable the deletion of target files and directories, 
add the system property jswap.delete=true (-Djswap.delete=true).
                                       
    java -javaagent:<pathTo>/jswap-agent.jar \
        -Djswap.from... \
        -Djswap.delete=true \
        ... SomeJavaClass


# External Libraries

JSwap uses for compile, run and test: 

* Guava (Apache License, Version 2.0)
* SpringLoaded (Apache License, Version 2.0)
* JUnit (BSD License)
* Hamcrest (BSD License)
* Mockito (MIT License)
	           
# License

Copyright 2014 Dominik Foerderreuther <dominik@eleon.de>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


	

