#!/usr/bin/env bash
# Starts sbt dev-web, waits for the webpack-dev-server to accept connections,
# issues a curl health check, then tears down. Exits non-zero on failure.
#
# Usage: ./scripts/test-dev-web.sh
set -euo pipefail

PORT=8080
TIMEOUT_SECS=150

# Keep a FIFO with an open write fd so sbt's stdin never hits EOF.
# Without it, sbt exits watch mode → JVM shutdown hooks kill webpack-dev-server.
FIFO=$(mktemp -u /tmp/sbt-stdin-XXXXXX)
mkfifo "$FIFO"
exec 9>"$FIFO"

cleanup() {
  exec 9>&- 2>/dev/null || true
  kill "$SBT_PID" 2>/dev/null || true
  wait "$SBT_PID" 2>/dev/null || true
  rm -f "$FIFO"
}
trap cleanup EXIT

sbt dev-web < "$FIFO" &
SBT_PID=$!

echo "Waiting for dev server at http://localhost:$PORT ..."
MAX=$((TIMEOUT_SECS / 5))
for i in $(seq 1 "$MAX"); do
  sleep 5
  if curl -sf --max-time 3 "http://localhost:$PORT/" > /dev/null 2>&1; then
    echo "Server ready after ~$((i * 5))s"
    break
  fi
  echo "  ($i/$MAX) still waiting..."
  if [ "$i" -eq "$MAX" ]; then
    echo "ERROR: dev server did not start within ${TIMEOUT_SECS}s"
    exit 1
  fi
done

HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$PORT/")
if [ "$HTTP_STATUS" != "200" ]; then
  echo "ERROR: expected HTTP 200, got $HTTP_STATUS"
  exit 1
fi
echo "HTTP $HTTP_STATUS – dev-web health check passed"
