@echo off
rem 
rem Licensed to the Apache Software Foundation (ASF) under one or more
rem contributor license agreements.  See the NOTICE file distributed with
rem this work for additional information regarding copyright ownership.
rem The ASF licenses this file to You under the Apache License, Version 2.0
rem (the "License"); you may not use this file except in compliance with
rem the License.  You may obtain a copy of the License at
rem
rem     http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

rem Batch script for defining the ProcrunService (JVM and Java versions)

rem Copy this file and door-proxy.jar into the same directory as prunsrv (or adjust the paths below)

setlocal

rem The service names (make sure they does not clash with an existing service)
set SERVICE_JVM=DoorProxyJvm
set SERVICE_JAVA=DoorProxyJava

rem my location
set MYPATH=%~dp0

rem location of Prunsrv
set PATH_PRUNSRV=%MYPATH%
set PR_LOGPATH=%PATH_PRUNSRV%
rem location of jarfile
set PATH_JAR=%MYPATH%
echo "%PATH_JAR%door-proxy.jar"

rem Allow prunsrv to be overridden
if "%PRUNSRV%" == "" set PRUNSRV=%PATH_PRUNSRV%amd64/prunsrv

rem Install the 2 services

echo Installing %SERVICE_JVM%
%PRUNSRV% //DS//%SERVICE_JVM%

echo Setting the parameters for %SERVICE_JVM%
%PRUNSRV% //IS//%SERVICE_JVM% --Startup=auto ^
 --Description="Door proxy client deamon service" ^
 --DisplayName="Door proxy" ^
 --Jvm=auto --StdOutput auto --StdError auto ^
 --JvmOptions=-Dkeystore.path=%PATH_JAR%client-store.pkcs ^
 --Classpath=%PATH_JAR%door-proxy.jar ^
 --StartPath=%PATH_JAR% ^
 --StartMode=jvm --StartClass=org.yyy.doorproxy.Bootstrap --StartMethod=start ^
 --StopMode=jvm  --StopClass=org.yyy.doorproxy.Bootstrap  --StopMethod=stop

echo Installation of %SERVICE_JVM% is complete

rem echo Installing %SERVICE_JAVA%
rem  %PRUNSRV% //DS//%SERVICE_JAVA%
rem  %PRUNSRV% //IS//%SERVICE_JAVA%

rem  echo Setting the parameters for %SERVICE_JAVA%
rem %PRUNSRV% //US//%SERVICE_JAVA% --Jvm=auto --StdOutput auto --StdError auto ^
rem  --Classpath=%PATH_JAR%door-proxy.jar ^
rem  --StartMode=java --StartClass=org.springframework.boot.loader.JarLauncher --StartParams=start ^
rem  --StopMode=java  --StopClass=org.springframework.boot.loader.JarLauncher  --StopParams=stop

rem  echo Installation of %SERVICE_JAVA% is complete
echo Finished