#!/usr/bin/env node
// Compile the graphics homework applets into the jars their pages serve.
//
// The source lives in two different shapes, which is the whole reason this is
// scripted rather than done by hand: Hw01-Hw05 sit loose in their own
// directory, while Hw06-Hw09 were only ever shipped as the .zip archive each
// page links to. Both are source. The jars are not -- they are gitignored the
// way /dist/ is, so what gets committed stays source only.
//
// Everything compiles with `--release 8`. CheerpJ runs applets on a Java 8
// runtime, and a newer class file version would simply fail to load there, so
// this has to hold even though the compiler itself is much newer.

import { execFileSync } from "node:child_process";
import { createWriteStream, existsSync } from "node:fs";
import { cp, mkdtemp, mkdir, readdir, readFile, rm, stat, writeFile } from "node:fs/promises";
import os from "node:os";
import path from "node:path";
import { Readable } from "node:stream";
import { pipeline } from "node:stream/promises";
import { fileURLToPath } from "node:url";

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const GRAPHICS = path.join(ROOT, "site", "graphics");
const CACHE = path.join(os.homedir(), ".cache", "brandonreiss-site", "jdk");
const JDK_VERSION = 21;

const ADOPTIUM_OS = { darwin: "mac", linux: "linux" };
const ADOPTIUM_ARCH = { arm64: "aarch64", x64: "x64" };

/* ------------------------------------------------------------------ jdk ---- */

async function findJavaHome(dir, depth = 0) {
  if (depth > 4) return null;
  if (existsSync(path.join(dir, "bin", "javac"))) return dir;
  for (const entry of await readdir(dir, { withFileTypes: true })) {
    if (!entry.isDirectory()) continue;
    const found = await findJavaHome(path.join(dir, entry.name), depth + 1);
    if (found) return found;
  }
  return null;
}

async function downloadJdk() {
  const osName = ADOPTIUM_OS[process.platform];
  const arch = ADOPTIUM_ARCH[process.arch];
  if (!osName || !arch) {
    throw new Error(
      `no Temurin build mapped for ${process.platform}/${process.arch}; ` +
        `set JAVA_HOME to a JDK that supports --release 8 instead`,
    );
  }

  const url =
    `https://api.adoptium.net/v3/binary/latest/${JDK_VERSION}/ga/` +
    `${osName}/${arch}/jdk/hotspot/normal/eclipse`;
  const tmp = await mkdtemp(path.join(os.tmpdir(), "jdk-"));
  console.log(`build-applets: downloading Temurin JDK ${JDK_VERSION} for ${osName}/${arch}`);

  try {
    const response = await fetch(url, { redirect: "follow" });
    if (!response.ok) throw new Error(`${url} -> HTTP ${response.status}`);
    const archive = path.join(tmp, "jdk.tgz");
    await pipeline(Readable.fromWeb(response.body), createWriteStream(archive));

    execFileSync("tar", ["-xzf", archive, "-C", tmp], { stdio: "pipe" });

    // macOS ships Contents/Home, Linux ships <name>/ -- walk to whichever has javac
    const home = await findJavaHome(tmp);
    if (!home) throw new Error("extracted archive contains no bin/javac");

    await mkdir(CACHE, { recursive: true });
    await cp(home, CACHE, { recursive: true });
  } finally {
    await rm(tmp, { recursive: true, force: true });
  }

  return CACHE;
}

async function locateJdk() {
  const candidates = [path.join(CACHE, "bin", "javac")];
  if (process.env.JAVA_HOME) candidates.push(path.join(process.env.JAVA_HOME, "bin", "javac"));
  for (const javac of candidates) if (existsSync(javac)) return path.dirname(path.dirname(javac));
  return downloadJdk();
}

/* ----------------------------------------------------------- homework ---- */

async function newest(paths) {
  let latest = 0;
  for (const file of paths) {
    const { mtimeMs } = await stat(file);
    if (mtimeMs > latest) latest = mtimeMs;
  }
  return latest;
}

async function sourcesFor(name, dir) {
  const loose = (await readdir(dir))
    .filter((f) => f.endsWith(".java"))
    .map((f) => path.join(dir, f));
  if (loose.length) return { files: loose, inputs: loose };

  const zip = path.join(dir, `${name}.zip`);
  if (!existsSync(zip)) return null;

  const extracted = await mkdtemp(path.join(os.tmpdir(), `${name}-`));
  execFileSync("unzip", ["-qo", zip, "-d", extracted], { stdio: "pipe" });
  const files = await collectJava(extracted);
  if (!files.length) throw new Error(`${name}.zip contains no .java files`);
  return { files, inputs: [zip], extracted };
}

async function collectJava(dir) {
  const found = [];
  for (const entry of await readdir(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) found.push(...(await collectJava(full)));
    else if (entry.name.endsWith(".java")) found.push(full);
  }
  return found;
}

/* ------------------------------------------------------- browser fix ------ */

// BufferedApplet comes in three generations across these homeworks. Nine of
// the ten sleep unconditionally in their render loop. Hw06 ships the later
// Perlin version, which suppresses that sleep while dontSleep is set -- and
// Hw06 is the only homework that sets it, because it renders a single
// scanline per repaint and wanted the next one started immediately.
//
// That was safe in 2012, when appletviewer gave it a real EDT to paint on.
// CheerpJ schedules the JVM cooperatively on the page's single JS thread, so a
// Java loop that never blocks never yields: repaint() stops being delivered
// and the demo freezes after its first scanline. Sleeping for a millisecond
// gives the page its turn back without changing what the loop is for, and the
// branch is dead code for the homeworks that never set dontSleep.
//
// The submitted source stays as submitted. This belongs beside --release 8
// and the *Test.java filter as a fact about the runtime the jar is built for,
// rather than a change to the assignment.
const SPIN_LOOP =
  /^([ \t]*)if \(!dontSleep && \(sleepMs > 0\)\) \{\r?\n[ \t]*t\.sleep\(sleepMs\);\r?\n[ \t]*\}/m;

function yieldingLoop(indent) {
  return [
    `${indent}if (dontSleep) {`,
    `${indent}  t.sleep(1);`,
    `${indent}} else if (sleepMs > 0) {`,
    `${indent}  t.sleep(sleepMs);`,
    `${indent}}`,
  ].join("\n");
}

async function applyYieldPatch(files) {
  for (const file of files) {
    if (path.basename(file) !== "BufferedApplet.java") continue;

    const source = await readFile(file, "utf8");
    if (!source.includes("dontSleep")) continue; // an older generation
    if (/if \(dontSleep\) \{/.test(source)) continue; // already rewritten

    const rewritten = source.replace(SPIN_LOOP, (_match, indent) => yieldingLoop(indent));
    if (rewritten === source) {
      throw new Error(
        `${file} sets dontSleep but its render loop no longer matches the shape ` +
          `this rewrite understands. Teach SPIN_LOOP the new shape rather than ` +
          `ship a loop that never yields -- CheerpJ will freeze it on row one.`,
      );
    }
    await writeFile(file, rewritten);
  }
}

async function build(name, jdk) {
  const dir = path.join(GRAPHICS, name);
  const jar = path.join(dir, `${name}.jar`);
  const source = await sourcesFor(name, dir);
  if (!source) return { name, skipped: true, reason: "no source" };

  try {
    // The three *Test.java files pull in JUnit, which is not shipped alongside
    // and has never been part of what the applet page runs.
    const files = source.files.filter((f) => !path.basename(f).endsWith("Test.java"));

    if (existsSync(jar) && (await stat(jar)).mtimeMs > (await newest(source.inputs))) {
      return { name, skipped: true, reason: "up to date" };
    }

    await applyYieldPatch(files);

    const out = await mkdtemp(path.join(os.tmpdir(), `${name}-out-`));
    try {
      execFileSync(
        path.join(jdk, "bin", "javac"),
        ["--release", "8", "-Xlint:-options", "-nowarn", "-d", out, ...files],
        { stdio: "pipe" },
      );
      execFileSync(path.join(jdk, "bin", "jar"), ["cf", jar, "-C", out, "."], { stdio: "pipe" });
    } finally {
      await rm(out, { recursive: true, force: true });
    }

    const { size } = await stat(jar);
    return { name, sources: files.length, bytes: size };
  } catch (error) {
    const detail = [error.stderr, error.stdout]
      .filter(Boolean)
      .map(String)
      .join("\n")
      .trim();
    throw new Error(`${name} failed to compile:\n${detail || error.message}`);
  } finally {
    if (source.extracted) await rm(source.extracted, { recursive: true, force: true });
  }
}

/* ----------------------------------------------------------------- main ---- */

const jdk = await locateJdk();

const homeworks = (await readdir(GRAPHICS, { withFileTypes: true }))
  .filter((entry) => entry.isDirectory() && /^Hw\d/.test(entry.name))
  .map((entry) => entry.name)
  .sort();

let compiled = 0;
let kept = 0;
let total = 0;

for (const name of homeworks) {
  const result = await build(name, jdk);
  if (result.skipped) {
    if (result.reason !== "up to date") console.log(`build-applets: ${name}: ${result.reason}`);
    else kept++;
    continue;
  }
  compiled++;
  total += result.bytes;
  console.log(
    `build-applets: ${name}: ${result.sources} sources -> ` +
      `${(result.bytes / 1024).toFixed(0)} KB`,
  );
}

console.log(
  compiled
    ? `build-applets: ${compiled} compiled, ${kept} already current -> site/graphics/`
    : `build-applets: all ${kept} jars already current`,
);
