# See here for more info: https://containers.dev/guide/dockerfile
ARG VARIANT="20-04"
FROM mcr.microsoft.com/devcontainers/base:ubuntu-22.04

# Install Java (OpenJDK 17 for Kotlin/Gradle)
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends openjdk-17-jdk \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Install Docker CLI
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends docker.io \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends libdvdnav-dev \
    && apt-get clean && rm -rf /var/lib/apt/lists/*
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends gradle \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# [Optional] Install 'container' CLI if it's a common tool for Apple container builds
# This part is speculative as 'container' is not a standard Linux package.
# If 'container' refers to a specific tool, it would need to be installed here.
# For now, we'll assume Docker is the primary container tool.

# Add a non-root user
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID
RUN if [ "$USER_UID" != "1000" ] || [ "$USER_GID" != "1000" ]; then \
        groupadd --gid $USER_GID $USERNAME \
        && useradd -s /bin/bash --uid $USER_UID --gid $USER_GID -m $USERNAME \
        && apt-get update \
        && apt-get install -y sudo \
        && echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/$USERNAME \
        && chmod 0440 /etc/sudoers.d/$USERNAME; \
    fi

RUN export JAVA_HOME_CANDIDATES=$(find /usr/lib/jvm -name "java-*-openjdk-*" -type d) && \
    export JAVA_HOME=$(echo "${JAVA_HOME_CANDIDATES}" | head -n 1) && \
    echo "export JAVA_HOME=${JAVA_HOME}" >> /etc/profile.d/jdk_home.sh && \
    echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> /etc/profile.d/jdk_home.sh

# Switch to non-root user
USER $USERNAME

# Set the working directory
WORKDIR /workspaces/phizz
