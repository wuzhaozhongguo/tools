#!/bin/sh
JAR_NAME=`ls *.jar | head -n 1 | awk '{print $0}'`
nohup java -jar ${JAR_NAME} > out.log &
echo $! > tpid
