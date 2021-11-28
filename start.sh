#!/bin/bash
sh gradlew shadowJar
java -jar /build/libs/GradleBot-1.0-SNAPSHOT-all.jar