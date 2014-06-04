# Welcome to WatchCopy

## What is WatchCopy?

WatchCopy is a JVM agent for synchronizing your IDEs or Mavens bin directory with the classpath of a running JVM. It uses 
Spring Loaded (https://github.com/spring-projects/spring-loaded/) for reloading changed classes during runtime. 

# Installation

Download the source and build it with maven.

    mvn clean install
    
The agent-jar will be target/watchcopy-agent.jar
    
# Running 

## Simple example of running with a single source and a single target folder

    java -javaagent:<pathTo>/watchcopy-agent.jar \
        -Dwatchcopy.from[0]=<pathOfYourIDEsBinDirectory> \
        -Dwatchcopy.to[0]=<pathOfYourClasspathDirectory>
        ... SomeJavaClass
        
## Example of running with Tomcat, synchronize classes of two modules and static resources

    SERVER=<pathTo>/server/apache-tomcat

	export JAVA_OPTS="$JAVA_OPTS -Ddev=true \
	        -Dcom.sun.management.jmxremote \
	        -noverify"

	# WatchCopy
	export JAVA_OPTS="$JAVA_OPTS -javaagent:<pathTo>/watchcopy-agent.jar \
	        -Dwatchcopy.from[0]=<yourMavenProject>/target/classes \
	        -Dwatchcopy.to[0]=${SERVER}/webapps/ROOT/WEB-INF/classes \
            -Dwatchcopy.from[1]=<anotherModul>/target/classes \
            -Dwatchcopy.to[1]=${SERVER}/webapps/ROOT/WEB-INF/classes \
            -Dwatchcopy.from[2]=<yourMavenProject>/src/main/webapps \
            -Dwatchcopy.to[2]=${SERVER}/webapps/ROOT"

	$SERVER/bin/catalina.sh jpda start


# Used Components

WatchCopy uses for compile, run and test: 

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


	

