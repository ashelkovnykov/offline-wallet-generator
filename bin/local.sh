#!/usr/bin/env sh

# ======================================================================================================================
#
# local - Wrapper script meant to make it easier to call the locally-compiled version of the offline-wallet-generator.
#         This script is a convenience feature for the user to avoid "java -jar" commands.
#
# ======================================================================================================================

java -jar `dirname "$0"`/../build/cli/libs/cli.jar $@
