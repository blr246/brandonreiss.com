#!/usr/bin/env bash
#
# Deploy dist/ -> s3://brandonreiss.com -> invalidate CloudFront.
#
#   ./scripts/deploy.sh              sync + invalidate
#   ./scripts/deploy.sh --dry-run    show what would transfer, touch nothing
#   ./scripts/deploy.sh --no-invalidate   skip the CloudFront invalidation
#
# Requires: aws CLI configured for account 814447972905 (us-east-1).
set -euo pipefail

BUCKET="brandonreiss.com"
REGION="us-east-1"
# Aliases: brandonreiss.com, www.brandonreiss.com
DISTRIBUTION_ID="EAW3ZQA2585VF"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIST="$ROOT/dist"

DRY_RUN=0
DO_INVALIDATE=1
for arg in "$@"; do
  case "$arg" in
    --dry-run)      DRY_RUN=1 ;;
    --no-invalidate) DO_INVALIDATE=0 ;;
    -h|--help)
      sed -n '2,10p' "${BASH_SOURCE[0]}"
      exit 0
      ;;
    *)
      echo "deploy: unknown option: $arg" >&2
      exit 2
      ;;
  esac
done

# --- preflight -------------------------------------------------------------
if ! command -v aws >/dev/null 2>&1; then
  echo "deploy: aws CLI not found on PATH" >&2
  exit 1
fi

if [[ ! -d "$DIST" ]]; then
  echo "deploy: $DIST not found — run 'npm run build' first" >&2
  exit 1
fi

# Confirm we are in the expected account before writing to production.
ACCOUNT="$(aws sts get-caller-identity --query Account --output text 2>/dev/null || echo "UNABLE")"
if [[ "$ACCOUNT" != "814447972905" ]]; then
  echo "deploy: refusing to run — caller account is '$ACCOUNT', expected '814447972905'" >&2
  exit 1
fi

echo "deploy: bucket  s3://$BUCKET ($REGION)"
echo "deploy: source  $DIST"
echo "deploy: account $ACCOUNT"
echo

# --- sync ------------------------------------------------------------------
# NOTE: no --delete. The bucket also holds noweats/data/ (37k objects) and
# private/ (1.2 GB), which are deliberately absent from dist/. A --delete run
# would erase them.
SYNC_ARGS=(s3 sync "$DIST/" "s3://$BUCKET/" --region "$REGION")

if [[ "$DRY_RUN" -eq 1 ]]; then
  SYNC_ARGS+=(--dryrun)
  echo "deploy: DRY RUN — no objects will be written"
  echo
fi

aws "${SYNC_ARGS[@]}"

# --- cache headers ----------------------------------------------------------
# `aws s3 sync` only uploads files whose bytes changed, so it never touches an
# otherwise up-to-date object and would never apply (or update) its metadata.
# This pass covers every file under dist/, reading each object's existing
# Content-Type back from S3 so MIME types are preserved rather than re-sniffed.
# It only ever lists files that exist in dist/, so noweats/data/ and private/
# are never modified.
echo
if [[ "$DRY_RUN" -eq 1 ]]; then
  node "$ROOT/scripts/set-cache.mjs" --dry-run
else
  node "$ROOT/scripts/set-cache.mjs"
fi

if [[ "$DRY_RUN" -eq 1 ]]; then
  echo
  echo "deploy: dry run complete. Nothing was uploaded."
  exit 0
fi

# --- invalidate CloudFront -------------------------------------------------
# The origin is the S3 website endpoint; without this the CDN keeps serving
# stale objects (observed age: 85131s ≈ 23.6h).
if [[ "$DO_INVALIDATE" -eq 1 ]]; then
  echo
  echo "deploy: invalidating CloudFront distribution $DISTRIBUTION_ID"
  INVALIDATION_ID="$(aws cloudfront create-invalidation \
    --distribution-id "$DISTRIBUTION_ID" \
    --paths "/*" \
    --query 'Invalidation.Id' \
    --output text)"
  echo "deploy: invalidation $INVALIDATION_ID created"
  echo
  echo "deploy: track with:"
  echo "  aws cloudfront get-invalidation --distribution-id $DISTRIBUTION_ID --id $INVALIDATION_ID"
else
  echo
  echo "deploy: SKIPPED CloudFront invalidation — the CDN may serve stale"
  echo "deploy: content for up to 24h. Pass no flag to invalidate."
fi

echo
echo "deploy: done -> https://brandonreiss.com/"
