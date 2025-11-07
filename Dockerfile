FROM ubuntu:22.04

# Install native dependencies and Java
RUN apt-get update && \
    apt-get install -y libdvdnav-dev libbluray-dev openjdk-17-jdk && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy the built application
COPY build/libs/phizz.jar /app/phizz.jar

# Run the application
CMD ["java", "-jar", "/app/phizz.jar"]
