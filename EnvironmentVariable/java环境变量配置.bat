@echo off
cd /d %~dp0
start %CD%\jdk7.exe
set /p javaPath=please input java path:
SETX JAVA_HOME "%javaPath%" /m
SETX CLASSPATH "%javaPath%\lib\dt.jar;%javaPath%\lib\tools.jar" /m
SETX Path "%javaPath%\bin;%javaPath%\jre\bin;%Path%" /m
PAUSE