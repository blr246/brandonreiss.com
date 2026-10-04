#!/usr/bin/env node
/**
 * Serve: static preview of dist/ with no file watching.
 * Useful for eyeballing the build output exactly as it will deploy.
 */
import liveServer from "live-server";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { createLiveProxy } from "./proxy.mjs";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const port = Number(process.env.PORT) || 8080;

liveServer.start({
  root: path.join(root, "dist"),
  port,
  host: "127.0.0.1",
  open: false,
  wait: 200,
  logLevel: 2,
  // Excluded datasets (noweats/data) are fetched from the live site instead
  // of being mirrored locally. Runs before the static handler.
  middleware: [createLiveProxy()],
});

console.log(`serve: http://127.0.0.1:${port}/ (dist/, no watching)`);
