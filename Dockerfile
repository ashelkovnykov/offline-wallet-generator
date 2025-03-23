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

# Default output directory as an absolute path, can be overridden during docker build or run
ARG OUTPUT_DIR="/app/output"
ENV OUTPUT_DIR=${OUTPUT_DIR}

# Create output directory
RUN mkdir -p ${OUTPUT_DIR}

# Copy only the necessary files from the builder stage
COPY --from=builder /app/offline-wallet-generator/bin/release.sh ./bin/
COPY --from=builder /app/offline-wallet-generator/lib/ ./lib/
COPY --from=builder /app/offline-wallet-generator/bin/entrypoint.sh ./bin/
RUN chmod +x ./bin/entrypoint.sh

ENTRYPOINT ["./bin/entrypoint.sh"]

# Default to showing help if no arguments are provided
CMD ["--help"]
