#!/usr/bin/env node
/**
 * Build: incremental sync of site/ -> dist/
 *
 * The site is plain static files using root-absolute paths (/style.css, /lib/,
 * /graphics/), so the "build" is a copy. Kept as a distinct step so a real
 * generator can be dropped in later without touching dev/serve/deploy.
 *
 * The sync is INCREMENTAL and preserves mtimes on purpose:
 *   - an unchanged file is skipped, so its mtime in dist/ does not move;
 *   - a changed file is copied with the source's timestamp.
 * This matters because dev.mjs watches dist/ to trigger browser reloads. A
 * naive "rm -rf + copy" rewrites all 173 files on every save, which makes the
 * watcher fire hundreds of spurious reload events per keystroke.
 */
import { cp, mkdir, readdir, rm, stat } from "node:fs/promises";
import { existsSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const src = path.join(root, "site");
const dest = path.join(root, "dist");

if (!existsSync(src)) {
  console.error(`build: missing source directory ${src}`);
  process.exit(1);
}

const t0 = Date.now();

/** Recursively list files under dir as paths relative to dir. */
async function walk(dir, base = dir, out = []) {
  const entries = await readdir(dir, { withFileTypes: true });
  for (const entry of entries) {
    const abs = path.join(dir, entry.name);
    if (entry.isDirectory()) await walk(abs, base, out);
    else if (entry.isFile()) out.push(path.relative(base, abs));
  }
  return out;
}

const srcFiles = await walk(src);
const destFiles = existsSync(dest) ? await walk(dest) : [];
const destSet = new Set(destFiles);

let copied = 0;
let skipped = 0;

for (const rel of srcFiles) {
  const from = path.join(src, rel);
  const to = path.join(dest, rel);

  let needsCopy = false;
  if (!existsSync(to)) {
    needsCopy = true;
  } else {
    const [a, b] = await Promise.all([stat(from), stat(to)]);
    // Size or timestamp drift means the source changed.
    needsCopy = a.size !== b.size || Math.abs(a.mtimeMs - b.mtimeMs) > 1000;
  }

  if (needsCopy) {
    await mkdir(path.dirname(to), { recursive: true });
    // preserveTimestamps keeps dist/ mtime == site/ mtime so the watcher
    // only fires for files that actually changed.
    await cp(from, to, { force: true, preserveTimestamps: true });
    copied++;
  } else {
    skipped++;
  }
}

// Remove files deleted from the source.
const srcSet = new Set(srcFiles);
let deleted = 0;
for (const rel of destFiles) {
  if (!srcSet.has(rel)) {
    await rm(path.join(dest, rel), { force: true });
    deleted++;
  }
}

// Prune directories left empty by deletions, deepest first.
async function pruneEmptyDirs(dir) {
  if (!existsSync(dir) || dir === dest) return;
  const entries = await readdir(dir, { withFileTypes: true });
  for (const entry of entries) {
    if (entry.isDirectory()) await pruneEmptyDirs(path.join(dir, entry.name));
  }
  const remaining = await readdir(dir);
  if (remaining.length === 0) await rm(dir, { recursive: true, force: true });
}
if (deleted > 0) await pruneEmptyDirs(dest);

const ms = Date.now() - t0;
const summary = [
  copied ? `${copied} copied` : null,
  skipped ? `${skipped} unchanged` : null,
  deleted ? `${deleted} removed` : null,
]
  .filter(Boolean)
  .join(", ");

console.log(`build: ${summary} -> dist/ (${ms}ms)`);
