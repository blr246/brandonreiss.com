#!/usr/bin/env node
/**
 * Dev: build once, watch site/, rebuild on change, live-reload the browser.
 *
 * Flow:  edit site/**  ->  rebuild dist/  ->  live-server notices dist/ change
 *        and pushes a reload to the browser.
 */
import { spawn } from "node:child_process";
import { watch, existsSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";
import liveServer from "live-server";
import { createLiveProxy } from "./proxy.mjs";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const src = path.join(root, "site");
const port = Number(process.env.PORT) || 8080;

if (!existsSync(src)) {
  console.error(`dev: missing source directory ${src}`);
  process.exit(1);
}

function build() {
  return new Promise((resolve) => {
    const p = spawn(process.execPath, [path.join(root, "scripts", "build.mjs")], {
      stdio: "inherit",
    });
    p.on("exit", (code) => resolve(code === 0));
  });
}

// Initial build must succeed before we serve.
if (!(await build())) {
  console.error("dev: initial build failed");
  process.exit(1);
}

// Debounce bursts of change events (editors often fire several per save).
let pending = null;
let building = false;

async function scheduleRebuild(reason) {
  if (pending) clearTimeout(pending);
  pending = setTimeout(async () => {
    pending = null;
    if (building) return scheduleRebuild(reason);
    building = true;
    console.log(`\ndev: change detected (${reason}) — rebuilding...`);
    await build();
    building = false;
  }, 120);
}

watch(src, { recursive: true }, (_event, filename) => {
  scheduleRebuild(filename ? filename.toString() : "unknown");
});

liveServer.start({
  root: path.join(root, "dist"),
  port,
  host: "127.0.0.1",
  open: false,
  wait: 300,
  logLevel: 2,
  // Excluded datasets (noweats/data) are fetched from the live site instead
  // of being mirrored locally. Runs before the static handler.
  middleware: [createLiveProxy()],
});

console.log(`\ndev: watching site/ -> dist/ -> http://127.0.0.1:${port}/`);
console.log("dev: edit files under site/ and the browser will reload.");
