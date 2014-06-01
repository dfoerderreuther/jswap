# Welcome to WatchCopy

## What is WatchCopy?

WatchCopy is a JVM agent for synchronizing your IDEs or mavens bin directory with the running classpath. Combined with 
Spring Loaded (https://github.com/spring-projects/spring-loaded/) you get an alternative to jrebel. 

# Installation

Dowload the source and build it with maven.

    mvn clean install
    
The agent-jar will be target/watchcopy-agent.jar
    
# Running 

    java -javaagent:<pathTo>/watchcopy-agent.jar \
        -Dwatchcopy.from=<pathOfYourIDEsBinDirectory> \
        -Dwatchcopy.to=<pathOfYourRunningDirectory>
        ... SomeJavaClass
        
# Running with Spring Loaded 

    java -javaagent:<pathTo>/watchcopy-agent.jar \
        -Dwatchcopy.from=<pathOfYourIDEsBinDirectory> \
        -Dwatchcopy.to=<pathOfYourRunningDirectory>
        -javaagent:<pathTo>/springloaded-{VERSION}.jar -noverify SomeJavaClass


# Running with Spring Loaded and tomcat

    SERVER=<pathTo>/server/apache-tomcat

	export JAVA_OPTS="$JAVA_OPTS -Ddev=true \
	        -Dcom.sun.management.jmxremote"

	export JAVA_OPTS="$JAVA_OPTS -noverify"

	# WatchCopy
	export JAVA_OPTS="$JAVA_OPTS -javaagent:<pathTo>/watchcopy-agent.jar \
	        -Dwatchcopy.from=<yourMavenProject>/target/classes \
	        -Dwatchcopy.to=${SERVER}/webapps/ROOT/WEB-INF/classes"

	# SpringLoaded
	export JAVA_OPTS="$JAVA_OPTS -javaagent:<pathTo>/springloaded-1.2.0.RELEASE.jar"

	$SERVER/bin/catalina.sh jpda start
	
	

