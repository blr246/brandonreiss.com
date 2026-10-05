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
import { cp, mkdir, readdir, readFile, rm, stat, writeFile } from "node:fs/promises";
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

// --- nav ---------------------------------------------------------------------
// The nav is hand-copied into every content page rather than templated, so
// this is the only thing keeping those copies honest. It runs before anything
// is written so a drifted build fails instead of shipping.
const NAV_OPEN = `<nav id="nav">`;
const NAV_CLOSE = "</nav>";

const navVersions = new Map(); // normalised block -> files that carry it
let navProblem = false;

for (const rel of srcFiles) {
  if (!rel.endsWith(".html")) continue;
  const html = await readFile(path.join(src, rel), "utf8");

  const open = html.indexOf(NAV_OPEN);
  const close = open < 0 ? -1 : html.indexOf(NAV_CLOSE, open);
  const block = open < 0 || close < 0 ? null : html.slice(open, close + NAV_CLOSE.length);
  const linked = html.includes("/nav.css");

  if (block === null && linked) {
    console.error(`build: ${rel} links /nav.css but has no nav block`);
    navProblem = true;
    continue;
  }
  if (block !== null && !linked) {
    console.error(`build: ${rel} has a nav block but no /nav.css link`);
    navProblem = true;
    continue;
  }
  if (block === null) continue;

  const marks = block.match(/aria-current="page"/g);
  if (marks && marks.length > 1) {
    console.error(`build: ${rel} marks ${marks.length} links as the current page`);
    navProblem = true;
  }

  const key = block.replace(/ aria-current="page"/g, "");
  if (!navVersions.has(key)) navVersions.set(key, []);
  navVersions.get(key).push(rel);
}

if (navVersions.size > 1) {
  console.error(`build: nav markup has drifted into ${navVersions.size} versions:`);
  let n = 0;
  for (const files of navVersions.values()) {
    console.error(`  version ${++n}:`);
    for (const f of files) console.error(`    ${f}`);
  }
  navProblem = true;
}
if (navProblem) process.exit(1);

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

// Remove files deleted from the source. Generated artifacts are exempt so
// they are not deleted and re-created (which would fire watcher events).
const GENERATED = new Set(["sitemap.xml"]);
const srcSet = new Set(srcFiles);
let deleted = 0;
for (const rel of destFiles) {
  if (GENERATED.has(rel)) continue;
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

// --- sitemap ---------------------------------------------------------------
// Generated from the tree rather than hand-maintained, so it cannot go stale.
// Every directory index becomes a trailing-slash URL; standalone content
// pages that are not index.html are listed explicitly. resume/resume.html is
// intentionally absent — it is fetched into resume/index.html via $.get and
// is not a page anyone navigates to.
const CANONICAL_ORIGIN = "https://brandonreiss.com";
const EXTRA_URLS = ["/graphics/aboutme.html"];

const urls = new Set();
for (const rel of srcFiles) {
  const posix = rel.split(path.sep).join("/");
  if (posix === "index.html") urls.add("/");
  else if (posix.endsWith("/index.html")) urls.add("/" + posix.slice(0, -"index.html".length));
}
for (const u of EXTRA_URLS) urls.add(u);

const sitemap =
  '<?xml version="1.0" encoding="UTF-8"?>\n' +
  '<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n' +
  [...urls]
    .sort()
    .map((u) => `  <url><loc>${CANONICAL_ORIGIN}${u}</loc></url>\n`)
    .join("") +
  "</urlset>\n";

await mkdir(dest, { recursive: true });
await writeFile(path.join(dest, "sitemap.xml"), sitemap, "utf8");

const ms = Date.now() - t0;
const summary = [
  copied ? `${copied} copied` : null,
  skipped ? `${skipped} unchanged` : null,
  deleted ? `${deleted} removed` : null,
  `sitemap ${urls.size} urls`,
]
  .filter(Boolean)
  .join(", ");

console.log(`build: ${summary} -> dist/ (${ms}ms)`);
