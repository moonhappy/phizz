#!/bin/bash
set -e

# Build the Kotlin application
./gradlew clean build

# Build the container image
if command -v container &> /dev/null
then
    container build -t phizz:latest .
else
    docker build -t phizz:latest .
fi

echo "Build complete. Run with: container run -it --rm phizz:latest"
