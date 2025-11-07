#!/bin/bash
set -e

# Build the Kotlin application
./gradlew clean build

# Build the Docker image
container build -t phizz:latest .

echo "Build complete. Run with: container run -it --rm phizz:latest"
