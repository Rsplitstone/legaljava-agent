@echo off
cd /d "C:\Users\Green\OneDrive\Documents\Legal_Java\backend"
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot
set PATH=%PATH%;%JAVA_HOME%\bin
gradlew.bat bootRun --args="--spring.profiles.active=local"
