#!/bin/bash
# This script serves as the entrypoint for the Docker container
# It prepares the output directory and passes all parameters to the jar

# Make sure output directory exists
mkdir -p ${OUTPUT_DIR}

# Execute the wallet generator jar with all passed arguments
java -jar ./lib/offline-wallet-generator-latest.jar "$@"
