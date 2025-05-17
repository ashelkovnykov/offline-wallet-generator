#!/usr/bin/env sh

# ======================================================================================================================
#
# docker/build - Helper script for building the project as a local Docker image.
#
# ======================================================================================================================

set -e

# Get location of script in file system
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
# Get project root
ROOT_DIR="${SCRIPT_DIR}/../.."
# Set the version of the wallet generator
VERSION=${1:-latest}

# Build the Docker image
docker build -t owg:${VERSION} $ROOT_DIR
