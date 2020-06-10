#!/bin/sh
UN_PARAM_JAVA_OPTSS=${1}
JAVA_HOME=${2}
./kill.sh
./start.sh "${RUN_PARAM_JAVA_OPTS}" "${JAVA_HOME}"
