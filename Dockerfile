# syntax=docker/dockerfile:1

FROM ubuntu:20.04
WORKDIR /app

RUN apt update
RUN DEBIAN_FRONTEND=noninteractive TZ=Etc/UTC apt -y install tzdata
RUN apt install -y git
RUN apt install -y openjdk-16-jdk

# Clone the repository
RUN git clone https://github.com/ashelkovnykov/offline-wallet-generator.git

# Default output directory, can be overridden during docker build or run
ARG OUTPUT_DIR="../output"
ENV OUTPUT_DIR=${OUTPUT_DIR}

# Create output directory
RUN mkdir -p ${OUTPUT_DIR}

# Build the application
WORKDIR /offline-wallet-generator/
RUN ./gradlew.sh clean build

# Copy the entrypoint script from host
COPY bin/entrypoint.sh ./bin/
# Make the entrypoint script executable
RUN chmod +x ./bin/entrypoint.sh

# Use our custom entrypoint script
ENTRYPOINT ["./bin/entrypoint.sh"]

# Default to showing help if no arguments are provided
CMD ["--help"]
