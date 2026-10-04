#!/usr/bin/env node
//
// Set Cache-Control on every object in dist/ that lives in the bucket.
//
//   node scripts/set-cache.mjs             apply the policy
//   node scripts/set-cache.mjs --dry-run   report what would change, touch nothing
//
// Why this exists: `aws s3 sync` only uploads files whose bytes changed, so it
// never updates metadata on an object that is otherwise up to date. This pass
// covers those. It deliberately reads each object's existing Content-Type back
// from S3 rather than guessing from the extension, so MIME types are preserved
// byte-for-byte (they have drifted historically: 15 objects are
// application/javascript while 2 are text/javascript).
//
// Only files present under dist/ are touched. noweats/data/ and private/ are
// never listed here, so they are never modified.
import { execFile } from "node:child_process";
import { promisify } from "node:util";
import { readdir } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const run = promisify(execFile);

const BUCKET = "brandonreiss.com";
const REGION = "us-east-1";

// Policy: HTML can go stale quickly and is cheap to revalidate, so keep it
// short. Everything else (css/js/img/fonts) changes rarely and is invalidated
// by CloudFront on deploy, so cache it for a day. No asset fingerprinting means
// anything longer would show returning visitors stale styles.
const HTML_CACHE = "max-age=3600"; // 1 hour
const ASSET_CACHE = "max-age=86400"; // 1 day

const CONCURRENCY = 8;
const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const DIST = path.join(ROOT, "dist");

const DRY_RUN = process.argv.includes("--dry-run");

function cacheFor(key) {
  return key.toLowerCase().endsWith(".html") ? HTML_CACHE : ASSET_CACHE;
}

// S3 keys are "/" separated; encode each segment but keep the separators.
function encodeKey(key) {
  return key.split("/").map(encodeURIComponent).join("/");
}

async function aws(args) {
  const { stdout } = await run("aws", args, { maxBuffer: 64 * 1024 * 1024 });
  return stdout;
}

async function walk(dir, base = "") {
  const out = [];
  for (const entry of await readdir(dir, { withFileTypes: true })) {
    const rel = base ? `${base}/${entry.name}` : entry.name;
    if (entry.isDirectory()) {
      out.push(...(await walk(path.join(dir, entry.name), rel)));
    } else {
      out.push(rel);
    }
  }
  return out;
}

// Hand each worker the next index until the list is exhausted.
async function pool(items, worker) {
  const results = new Array(items.length);
  let next = 0;
  await Promise.all(
    Array.from({ length: Math.min(CONCURRENCY, items.length) }, async () => {
      while (next < items.length) {
        const i = next++;
        results[i] = await worker(items[i]);
      }
    }),
  );
  return results;
}

async function applyOne(key) {
  const head = JSON.parse(
    await aws([
      "s3api",
      "head-object",
      "--bucket",
      BUCKET,
      "--key",
      key,
      "--region",
      REGION,
      "--output",
      "json",
    ]),
  );

  const desired = cacheFor(key);
  const current = head.CacheControl ?? null;

  if (current === desired) {
    return { key, status: "unchanged", desired, current };
  }

  if (DRY_RUN) {
    return { key, status: "would-set", desired, current };
  }

  await aws([
    "s3api",
    "copy-object",
    "--bucket",
    BUCKET,
    "--key",
    key,
    "--copy-source",
    `${BUCKET}/${encodeKey(key)}`,
    "--metadata-directive",
    "REPLACE",
    // Preserve the existing type exactly; do not let the CLI re-sniff it.
    "--content-type",
    head.ContentType,
    "--cache-control",
    desired,
    // Object currently carries ServerSideEncryption: AES256; state it so a
    // REPLACE does not drop it if bucket default encryption ever changes.
    "--server-side-encryption",
    head.ServerSideEncryption ?? "AES256",
    "--region",
    REGION,
    "--output",
    "json",
  ]);

  return { key, status: "set", desired, current };
}

const main = async () => {
  const keys = (await walk(DIST)).sort();
  if (keys.length === 0) {
    console.error(`set-cache: ${DIST} is empty — run 'npm run build' first`);
    process.exit(1);
  }

  console.log(`set-cache: bucket s3://${BUCKET} (${REGION})`);
  console.log(`set-cache: source ${DIST} (${keys.length} files)`);
  console.log(`set-cache: policy html=${HTML_CACHE}  assets=${ASSET_CACHE}`);
  if (DRY_RUN) console.log("set-cache: DRY RUN — no objects will be written");
  console.log();

  const results = await pool(keys, async (key) => {
    try {
      return await applyOne(key);
    } catch (err) {
      return { key, status: "error", error: String(err.stderr || err.message || err).trim() };
    }
  });

  const counts = {};
  for (const r of results) counts[r.status] = (counts[r.status] || 0) + 1;

  const errors = results.filter((r) => r.status === "error");
  for (const e of errors.slice(0, 10)) {
    console.log(`  ERROR ${e.key}`);
    console.log(`        ${e.error.split("\n")[0]}`);
  }
  if (errors.length > 10) console.log(`  ... and ${errors.length - 10} more errors`);
  if (errors.length) console.log();

  const touched = results.filter((r) => r.status === "set" || r.status === "would-set");
  const sample = touched.slice(0, 8);
  for (const t of sample) {
    console.log(
      `  ${t.status === "set" ? "SET  " : "would"} ${t.key}  ` +
        `${t.current ?? "(none)"} -> ${t.desired}`,
    );
  }
  if (touched.length > sample.length) {
    console.log(`  ... and ${touched.length - sample.length} more`);
  }
  if (touched.length) console.log();

  console.log(
    `set-cache: ${counts.set || 0} set, ` +
      `${counts["would-set"] || 0} would set, ` +
      `${counts.unchanged || 0} unchanged, ` +
      `${errors.length} errors`,
  );

  if (DRY_RUN) console.log("set-cache: dry run complete. Nothing was written.");

  if (errors.length) process.exit(1);
};

main().catch((err) => {
  console.error("set-cache:", err.stack || err);
  process.exit(1);
});
