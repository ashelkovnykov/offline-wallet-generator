#!/bin/bash
# This script builds the Docker image
# It is meant to be run from the root of the repository

# Set the version of the wallet generator
VERSION=${1:-latest}

# Build the Docker image
docker build -t github.com/ashelkovnykov/offline-wallet-generator:${VERSION} .
