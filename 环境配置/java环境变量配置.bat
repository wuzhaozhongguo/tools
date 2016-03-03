@echo off 
@set /p javaPath=请输入java安装路径:
@SETX JAVA_HOME "%javaPath%" /m
@SETX CLASSPATH "%javaPath%\lib\dt.jar;%javaPath%\lib\tools.jar" /m
@SETX Path "%javaPath%\bin;%javaPath%\jre\bin;%Path%" /m
@PAUSE