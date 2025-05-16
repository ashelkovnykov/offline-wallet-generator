#!/usr/bin/env sh

# ======================================================================================================================
#
# docker/local - Wrapper script meant to make it easier to call the locally-compiled Docker version of the
#                offline-wallet-generator. This script will filter the '-o' argument automatically, and instead assign
#                it as the mounted output location for the Docker containter.
#
# ======================================================================================================================

# Initialize an array to hold the filtered arguments
filtered_args=()

# Default output location
OUTPUT_DIR="./"

# Process the arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
        -o)
            # If '-o' is found, shift to get the next argument and echo it
            shift
            if [[ -n "$1" ]]; then
                OUTPUT_DIR="$1"
                shift
            fi
            ;;
        *)
            # Save all other arguments
            filtered_args+=("$1")
            shift
            ;;
    esac
done

# Create output dir and run Docker container
mkdir -p $OUTPUT_DIR
docker run --rm -it -v $OUTPUT_DIR:/app/output/:rw owg:latest "${filtered_args[@]}"
