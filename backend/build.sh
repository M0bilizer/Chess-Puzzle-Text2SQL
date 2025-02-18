#!/bin/bash

./gradlew build
docker build -t kotlin-spring .