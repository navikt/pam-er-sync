#!/usr/bin/env bash
# local-sync-test.sh — build, run, sync and verify, then clean up
set -euo pipefail

APP_URL="http://localhost:9012"
OS_URL="http://localhost:9200"
GAR_HOST="europe-north1-docker.pkg.dev"

BOLD='\033[1m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
RESET='\033[0m'

info()  { echo -e "${BOLD}==> $*${RESET}"; }
ok()    { echo -e "${GREEN}✓ $*${RESET}"; }
warn()  { echo -e "${YELLOW}! $*${RESET}"; }
fail()  { echo -e "${RED}✗ $*${RESET}" >&2; exit 1; }

COMPOSE_STARTED=0

cleanup() {
  [ "$COMPOSE_STARTED" -eq 1 ] || return 0
  info "Cleaning up Docker Compose..."
  docker compose down -v --remove-orphans
}
trap cleanup EXIT

# ── 0. Check required tools ───────────────────────────────────────────────────
MISSING=()
for tool in jq curl docker; do
  command -v "$tool" >/dev/null 2>&1 || MISSING+=("$tool")
done
if [ "${#MISSING[@]}" -gt 0 ]; then
  fail "Missing required tool(s): ${MISSING[*]} — install them and try again (e.g. 'brew install ${MISSING[*]}')"
fi

# ── 0b. Check Docker daemon ───────────────────────────────────────────────────
# Docker Desktop/Colima kan være installert uten å kjøre. Da feiler alt senere
# med "failed to connect to the docker API ... daemon is running".
docker_ready() { docker info >/dev/null 2>&1; }

wait_for_docker() {
  for _ in $(seq 1 30); do
    docker_ready && return 0
    sleep 2
  done
  return 1
}

if docker_ready; then
  ok "Docker daemon is reachable"
elif command -v colima >/dev/null 2>&1; then
  warn "Docker daemon is not reachable — starting Colima..."
  colima start || fail "'colima start' failed — check 'colima status'"
  wait_for_docker || fail "Colima started, but the Docker daemon is still unreachable — check 'colima status' and 'docker context ls'"
  ok "Colima started — Docker daemon is reachable"
else
  fail "Docker daemon is not running. Start Docker Desktop, or install and start Colima ('brew install colima && colima start'), then try again"
fi

# ── 1. Check gcloud auth ──────────────────────────────────────────────────────
# Innlogging trengs bare for å hente base-imaget. Ligger det allerede lokalt,
# hopper vi over, slik at skriptet virker offline og i CI.
BASE_IMAGE=$(awk '/^FROM/ {print $2; exit}' Dockerfile)

# Referansen i Dockerfile er på formen repo:tag@sha256:… Docker lagrer tag
# (RepoTags) og digest (RepoDigests) hver for seg, så den kombinerte
# referansen matcher ikke alltid et image som faktisk ligger lokalt.
base_image_present() {
  local no_digest="${BASE_IMAGE%@*}" ref
  local refs=("$BASE_IMAGE" "$no_digest")
  if [ "$BASE_IMAGE" != "$no_digest" ]; then
    refs+=("${no_digest%:*}@${BASE_IMAGE#*@}")
  fi
  for ref in "${refs[@]}"; do
    docker image inspect "$ref" >/dev/null 2>&1 && return 0
  done
  return 1
}

docker_cred_helper_configured() {
  jq -e --arg h "$GAR_HOST" \
    '(.credHelpers // {}) | has($h)' "${DOCKER_CONFIG:-$HOME/.docker}/config.json" >/dev/null 2>&1
}

if [ "${SKIP_GCLOUD_AUTH:-0}" = "1" ]; then
  warn "SKIP_GCLOUD_AUTH=1 — skipping gcloud check"
elif base_image_present; then
  ok "Base image already present locally — skipping gcloud check"
else
  info "Checking gcloud Docker credentials for $GAR_HOST..."
  if docker_cred_helper_configured || docker-credential-gcloud list 2>/dev/null | grep -q "$GAR_HOST"; then
    ok "Already authenticated with gcloud for $GAR_HOST"
  else
    warn "Not authenticated — running 'gcloud auth configure-docker $GAR_HOST'..."
    if ! gcloud auth print-access-token &>/dev/null; then
      warn "No active gcloud account. Running 'gcloud auth login'..."
      gcloud auth login || fail "gcloud auth login failed"
    fi
    gcloud auth configure-docker "$GAR_HOST" --quiet || fail "gcloud auth configure-docker failed"
    ok "gcloud Docker credentials configured"
  fi
fi

# ── 2. Build ──────────────────────────────────────────────────────────────────
info "Building app..."
./mvnw clean package -q --no-transfer-progress 2>&1 \
  | grep -E "^(\[ERROR\]|\[WARNING\] [^(])" \
  || true
ok "Build complete"

# ── 3. Start Docker Compose ───────────────────────────────────────────────────
info "Starting Docker Compose..."
COMPOSE_STARTED=1
docker compose up -d --build --quiet-pull 2>&1 \
  | grep -v "^#\|pulling\|Pulling\|waiting\|Waiting\|verifying\|Verifying\|Downloaded\|Pull complete\|digest:\|Status:" \
  || true
ok "Docker Compose started"

# ── 4. Wait for app to be ready ───────────────────────────────────────────────
info "Waiting for app to be ready..."
for i in $(seq 1 30); do
  if curl -sf "$APP_URL/isReady" > /dev/null 2>&1; then
    ok "App is ready"
    break
  fi
  [ "$i" -eq 30 ] && fail "App did not become ready after 30 attempts"
  sleep 3
done

# ── 5. Trigger sync endpoints ─────────────────────────────────────────────────
SYNCED_DATASETS=()

trigger_sync() {
  local name="$1" index="$2" endpoint="$3"
  info "Triggering sync: $name..."
  local code
  code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$APP_URL$endpoint")
  if [ "$code" = "200" ]; then
    ok "Sync $name triggered"
    SYNCED_DATASETS+=("$index")
  elif [ "$code" = "404" ]; then
    ok "Sync $name is disabled — skipping"
  else
    fail "Sync $name returned unexpected HTTP $code"
  fi
}

trigger_sync "underenheter" "UNDERENHET" "/internal/enhetsregister/sync/underenheter"
trigger_sync "hovedenheter" "HOVEDENHET" "/internal/enhetsregister/sync/hovedenheter"

[ "${#SYNCED_DATASETS[@]}" -eq 0 ] && fail "No datasets were synced — nothing to verify"

# ── 6. Verify data in OpenSearch ─────────────────────────────────────────────
POLL_INTERVAL=5
MAX_WAIT=60  # 60 polls × 5s = 5 minutes

for INDEX in "${SYNCED_DATASETS[@]}"; do
  # Appen tvinger indeksnavn til små bokstaver, og indeksnavn i OpenSearch er
  # case-sensitive. Uten dette matcher wildcarden ingenting og teller alltid 0.
  INDEX_LC=$(echo "$INDEX" | tr '[:upper:]' '[:lower:]')
  info "Waiting for $INDEX data in OpenSearch (up to $((MAX_WAIT * POLL_INTERVAL / 60)) min)..."
  for i in $(seq 1 $MAX_WAIT); do
    COUNT=$(curl -sf "$OS_URL/${INDEX_LC}*/_count" | jq -r '.count // 0' 2>/dev/null || echo 0)
    if [ "$COUNT" -gt 0 ]; then
      echo ""
      ok "$INDEX: $COUNT documents indexed"
      break
    fi
    if [ "$i" -eq "$MAX_WAIT" ]; then
      echo ""
      echo -e "${BOLD}App logs:${RESET}"
      docker compose logs app --tail=40
      fail "No documents found in $INDEX index after $((MAX_WAIT * POLL_INTERVAL))s — see app logs above"
    fi
    printf "\r    Elapsed: %ds — waiting for first documents..." $((i * POLL_INTERVAL))
    sleep $POLL_INTERVAL
  done

  ALIASES=$(curl -sf "$OS_URL/_aliases" \
    | jq -r --arg alias "$INDEX_LC" 'to_entries[] | select(.value.aliases | has($alias)) | .key')
  if [ -n "$ALIASES" ]; then
    ok "$INDEX alias is active, pointing to: $ALIASES"
  else
    ok "$INDEX: no alias yet (batch may still be running)"
  fi
done

# ── 7. Summary ────────────────────────────────────────────────────────────────
echo ""
echo -e "${BOLD}All checks passed!${RESET}"
for INDEX in "${SYNCED_DATASETS[@]}"; do
  INDEX_LC=$(echo "$INDEX" | tr '[:upper:]' '[:lower:]')
  # docs.deleted er overskrevne kopier. Indeksen slettes aldri før innlasting,
  # så en ny synk samme dag skriver inn i samme indeks og blåser opp diskbruken.
  curl -sf "$OS_URL/_cat/indices/${INDEX_LC}*?format=json" | jq -r '.[]
    | "  \(.index) : \(."docs.count") docs, \(."docs.deleted") deleted, \(."store.size")"'
done
echo ""
info "Press Ctrl+C to stop (cleanup runs automatically on exit)..."
# Keep running so developer can inspect dashboards / OpenSearch.
# NB: 'sleep infinity' finnes ikke i BSD sleep på macOS.
while true; do sleep 3600; done
