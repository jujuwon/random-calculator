#!/bin/bash

./gradlew clean build
java -jar ./build/libs/server-1.0-SNAPSHOT.jar