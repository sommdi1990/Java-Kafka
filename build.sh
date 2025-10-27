#!/bin/bash

set -e

# Check Java version >= 21
JAVA_VER=$(java -version 2>&1 | awk -F[\""] '/version/ {print $2}' | cut -d. -f1)
if [[ "$JAVA_VER" -lt 21 ]]; then
  echo "Java 21 is required. Current version: $JAVA_VER"
  exit 1
fi

echo "[INFO] Java version: $JAVA_VER"

echo "[INFO] Cleaning and building all modules..."
mvn clean install -DskipTests

echo "[INFO] Build finished successfully."
