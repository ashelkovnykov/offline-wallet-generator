# syntax=docker/dockerfile:1

# Build stage
FROM ubuntu:20.04 AS builder
WORKDIR /app

RUN apt update && \
    DEBIAN_FRONTEND=noninteractive TZ=Etc/UTC apt -y install tzdata && \
    apt install -y git openjdk-16-jdk

# Clone the repository
RUN git clone https://github.com/ashelkovnykov/offline-wallet-generator.git
WORKDIR /app/offline-wallet-generator/

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

# Copy our custom entrypoint script
COPY bin/entrypoint.sh ./bin/
RUN chmod +x ./bin/entrypoint.sh

# Use our custom entrypoint script
ENTRYPOINT ["./bin/entrypoint.sh"]

# Default to showing help if no arguments are provided
CMD ["--help"]
