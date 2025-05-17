#!/usr/bin/env sh

# Default output directory in the container
OUTPUT_DIR="/app/output"

# Create the output directory
mkdir -p "$OUTPUT_DIR"

# Add all passed arguments
ARGS="-o $OUTPUT_DIR"
ARGS="$ARGS $@"

java -jar ./bin/cli.jar $ARGS
