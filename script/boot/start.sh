#!/bin/sh
RUN_PARAM_JAVA_OPTS=${1}
JAR_NAME=`ls *.jar | head -n 1 | awk '{print $0}'`
nohup java -Djava.security.egd=file:/dev/./urandom ${RUN_PARAM_JAVA_OPTS} -jar ${JAR_NAME} &
echo $! > tpid
