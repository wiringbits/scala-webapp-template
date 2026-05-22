#!/usr/bin/env bash
# Starts sbt dev-web, waits for the webpack-dev-server to accept connections,
# issues a curl health check, then tears down. Exits non-zero on failure.
#
# Usage: ./scripts/test-dev-web.sh
set -euo pipefail

PORT=8080
TIMEOUT_SECS=150

# mktemp -d avoids the race condition of mktemp -u (unsafe temp name).
# The FIFO and log live inside the directory so cleanup is a single rm -rf.
WORK_DIR=$(mktemp -d)
FIFO="$WORK_DIR/sbt-stdin"
LOG="$WORK_DIR/sbt.log"
mkfifo "$FIFO"

# Open read/write so the exec never blocks waiting for a reader,
# and the write side stays open (no EOF) until we explicitly close fd 9.
exec 9<>"$FIFO"

# Initialize before the trap so cleanup never hits an unbound variable.
SBT_PID=""

cleanup() {
  exec 9>&- 2>/dev/null || true
  [ -n "${SBT_PID:-}" ] && kill "$SBT_PID" 2>/dev/null || true
  [ -n "${SBT_PID:-}" ] && wait "$SBT_PID" 2>/dev/null || true
  rm -rf "$WORK_DIR"
}
trap cleanup EXIT

sbt dev-web < "$FIFO" > "$LOG" 2>&1 &
SBT_PID=$!

echo "Waiting for dev server at http://localhost:$PORT ..."
MAX=$((TIMEOUT_SECS / 5))
for i in $(seq 1 "$MAX"); do
  sleep 5

  # Fail immediately if webpack-cli logged a fatal error.
  if grep -q "\[error\] \[webpack-cli\]" "$LOG" 2>/dev/null; then
    echo "Fatal webpack-cli error detected:"
    grep "\[error\]" "$LOG" >&2
    exit 1
  fi

  if curl -sf --max-time 3 "http://localhost:$PORT/" > /dev/null 2>&1; then
    echo "Server ready after ~$((i * 5))s"
    break
  fi
  echo "  ($i/$MAX) still waiting..."
  if [ "$i" -eq "$MAX" ]; then
    echo "ERROR: dev server did not start within ${TIMEOUT_SECS}s"
    echo "--- last 30 lines of sbt output ---" >&2
    tail -30 "$LOG" >&2
    exit 1
  fi
done

HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$PORT/")
if [ "$HTTP_STATUS" != "200" ]; then
  echo "ERROR: expected HTTP 200, got $HTTP_STATUS"
  exit 1
fi
echo "HTTP $HTTP_STATUS – dev-web health check passed"
