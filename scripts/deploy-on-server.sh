#!/usr/bin/env bash

set -euo pipefail

APP_HOME="${APP_HOME:-/opt/java-kafka}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.yml}"

echo ">>> Using APP_HOME=${APP_HOME}"
cd "${APP_HOME}"

if [[ ! -f "${COMPOSE_FILE}" ]]; then
  echo "compose file ${COMPOSE_FILE} not found in ${APP_HOME}" >&2
  exit 1
fi

echo ">>> Building/Pulling services"
docker compose -f "${COMPOSE_FILE}" pull || true
docker compose -f "${COMPOSE_FILE}" build --pull

echo ">>> Restarting stack"
docker compose -f "${COMPOSE_FILE}" up -d --remove-orphans

echo ">>> Cleanup old images"
docker image prune -af --filter "until=168h" || true

echo "Deployment finished successfully."

