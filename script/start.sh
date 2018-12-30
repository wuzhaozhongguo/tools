#!/bin/sh
JAR_NAME=`ls *.jar | head -n 1 | awk '{print $0}'`
nohup java -Djava.compiler=NONE -XX:ParallelGCThreads=2 -Xms64m -Xmx128m -jar ${JAR_NAME} > logs/out.log &
echo $! > tpid
