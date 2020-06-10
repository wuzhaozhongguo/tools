#!/bin/sh
RUN_PARAM_JAVA_OPTS=${1}
JAVA_HOME=${2}
JAR_NAME=`ls *.jar | head -n 1 | awk '{print $0}'`
nohup ${JAVA_HOME}/bin/java -Djava.security.egd=file:/dev/./urandom -jar ${RUN_PARAM_JAVA_OPTS} ${JAR_NAME} &
echo $! > tpid
