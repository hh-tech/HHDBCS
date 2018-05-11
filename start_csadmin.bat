@echo off 
if "%1" == "h" goto begin 
mshta vbscript:createobject("wscript.shell").run("%~nx0 h",0)(window.close)&&exit 
:begin
set JAVA_HOME=%~dp0jdk
set CLASSPATH=%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\lib\dt.jar
set ANT_HOME=%JAVA_HOME%\ant
set path=%JAVA_HOME%\bin;%ANT_HOME%\bin;%path%;
java -jar csadmin.jar