#!/usr/bin/env sh

# Check if OUTPUT_DIR environment variable is set
if [ ! -z "$OUTPUT_DIR" ]; then
  # Make sure the path is absolute
  case "$OUTPUT_DIR" in
    /*) # Path is already absolute
      ;;
    *) # Path is relative, make it absolute from current directory
      OUTPUT_DIR="$(pwd)/$OUTPUT_DIR"
      ;;
  esac
  
  # Create the output directory if it does not exist
  mkdir -p "$OUTPUT_DIR"
  
  # Check if -o or --output-file is already in the arguments
  OUTPUT_FLAG_FOUND=0
  for arg in "$@"; do
    if [ "$arg" = "-o" ] || [ "$arg" = "--output-file" ]; then
      OUTPUT_FLAG_FOUND=1
      break
    fi
  done
  
  # If output flag is not explicitly specified in arguments, add it
  if [ "$OUTPUT_FLAG_FOUND" -eq 0 ]; then
    ARGS="-o $OUTPUT_DIR"
  fi
fi

# Add all passed arguments
ARGS="$ARGS $@"

# Execute the original release.sh with all arguments
`dirname "$0"`/release.sh $ARGS
