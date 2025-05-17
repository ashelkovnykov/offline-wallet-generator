# syntax=docker/dockerfile:1

# Build stage
FROM ubuntu:20.04 AS builder
WORKDIR /app

RUN apt update && \
    DEBIAN_FRONTEND=noninteractive TZ=Etc/UTC apt -y install tzdata && \
    apt install -y openjdk-16-jdk

# Copy local source code instead of cloning from repo
COPY . /app/offline-wallet-generator/
WORKDIR /app/offline-wallet-generator/

# Make sure build script is executable
RUN chmod +x ./gradlew.sh

# Build the application
RUN ./gradlew.sh clean build

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install runtime dependencies only
RUN apt update && \
    DEBIAN_FRONTEND=noninteractive TZ=Etc/UTC apt -y install tzdata

# Copy only the necessary files from the builder stage
COPY --from=builder /app/offline-wallet-generator/docker/entrypoint.sh ./bin/
COPY --from=builder /app/offline-wallet-generator/build/cli/libs/cli.jar ./bin/
RUN chmod +x ./bin/entrypoint.sh

ENTRYPOINT ["./bin/entrypoint.sh"]

# Default to showing help if no arguments are provided
CMD ["--help"]
