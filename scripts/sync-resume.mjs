#!/usr/bin/env node
//
// Pull the current resume into the site.
//
//   npm run sync-resume
//   RESUME_SRC=/path/to/resume npm run sync-resume
//
// Why this exists: the resume is maintained in its own repository as a
// standalone document. Its stylesheet assumes it owns the page -- it sets
// `body`, `p`, and `h1-h3` globally -- and its HTML is a whole <html> document.
// Neither can be dropped onto a page that also carries the site's navigation,
// footer, and table art. So this copies the PDF verbatim, extracts just the
// .page content, and re-scopes every selector under `.resume`.
//
// The three generated files are committed, so this repo stays self-contained
// and builds whether or not the resume checkout is present.
import { readFile, writeFile } from "node:fs/promises";
import path from "node:path";
import os from "node:os";
import { fileURLToPath } from "node:url";

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const SRC = process.env.RESUME_SRC ?? path.join(os.homedir(), "Documents", "resume");
const OUT = path.join(ROOT, "site", "resume");

const BEGIN = "<!-- resume:begin -->";
const END = "<!-- resume:end -->";
const SCOPE = ".resume";

function fail(msg) {
  console.error(`sync-resume: ${msg}`);
  process.exit(1);
}

/* ---------------------------------------------------------------- html ---- */

// Pull out <div class="page"> ... </div>, counting nesting so the wrapper's own
// closing tag is the one we stop at.
function extractPage(html) {
  const open = html.search(/<div class="page"[^>]*>/);
  if (open < 0) fail(`no <div class="page"> found in ${path.join(SRC, "index.html")}`);

  const tag = /<\/?div\b/g;
  tag.lastIndex = open;
  let depth = 0;
  let m;
  while ((m = tag.exec(html))) {
    depth += m[0][1] === "/" ? -1 : 1;
    if (depth === 0) return html.slice(open, tag.lastIndex);
  }
  fail("unbalanced <div> inside .page");
}

function inject(page, content) {
  const b = page.indexOf(BEGIN);
  const e = page.indexOf(END);
  if (b < 0 || e < 0 || e < b) {
    fail(`site/resume/index.html needs ${BEGIN} and ${END} around the injection point`);
  }
  return page.slice(0, b + BEGIN.length) + "\n" + content + "\n    " + page.slice(e);
}

/* ----------------------------------------------------------------- css ---- */

function readTrivia(css, i) {
  let text = "";
  while (i < css.length) {
    if (/\s/.test(css[i])) { text += css[i++]; continue; }
    if (css[i] === "/" && css[i + 1] === "*") {
      const end = css.indexOf("*/", i + 2);
      const stop = end < 0 ? css.length : end + 2;
      text += css.slice(i, stop);
      i = stop;
      continue;
    }
    break;
  }
  return { text, i };
}

function readString(css, i) {
  const quote = css[i];
  let j = i + 1;
  while (j < css.length) {
    if (css[j] === "\\") { j += 2; continue; }
    if (css[j] === quote) return j + 1;
    j++;
  }
  return css.length;
}

function findClose(css, from) {
  let depth = 1;
  let i = from;
  while (i < css.length) {
    const ch = css[i];
    if (ch === "/" && css[i + 1] === "*") {
      const end = css.indexOf("*/", i + 2);
      i = end < 0 ? css.length : end + 2;
      continue;
    }
    if (ch === "'" || ch === '"') { i = readString(css, i); continue; }
    if (ch === "{") depth++;
    else if (ch === "}" && --depth === 0) return i;
    i++;
  }
  return css.length;
}

// Root-pseudo selectors have no element to anchor onto, so they collapse into
// the scope element itself.
function scopeSelector(selector, scope) {
  const scoped = selector.split(",").map((part) => {
    part = part.trim();
    if (part === "html" || part === "body") return scope;
    if (part === "*") return `${scope}, ${scope} *`;
    return `${scope} ${part}`;
  });
  return [...new Set(scoped)].join(", ");
}

// :root declares the design tokens, and @page sets print geometry -- both are
// meant to be document-wide, so they pass through untouched.
function passthrough(selector) {
  return selector === ":root" || (selector.startsWith("@") && !/^@media\b/.test(selector));
}

function scopeBlock(css, scope) {
  let out = "";
  let i = 0;

  while (i < css.length) {
    const trivia = readTrivia(css, i);
    out += trivia.text;
    i = trivia.i;
    if (i >= css.length) break;

    let prelude = "";
    while (i < css.length) {
      const ch = css[i];
      if (ch === "/" && css[i + 1] === "*") {
        const t = readTrivia(css, i);
        prelude += t.text;
        i = t.i;
        continue;
      }
      if (ch === "'" || ch === '"') {
        const stop = readString(css, i);
        prelude += css.slice(i, stop);
        i = stop;
        continue;
      }
      if (ch === "{" || ch === ";") break;
      prelude += ch;
      i++;
    }
    if (i >= css.length) { out += prelude; break; }

    if (css[i] === ";") { out += prelude + ";"; i++; continue; }

    const close = findClose(css, i + 1);
    const body = css.slice(i + 1, close);
    i = close + 1;

    const selector = prelude.trim();
    if (passthrough(selector)) {
      out += selector + "{" + body + "}";
    } else if (/^@media\b/.test(selector)) {
      out += selector + "{" + scopeBlock(body, scope) + "}";
    } else {
      out += scopeSelector(selector, scope) + "{" + body + "}";
    }
  }
  return out;
}

/* ---------------------------------------------------------------- main ---- */

const [sourceCss, sourceHtml] = await Promise.all([
  readFile(path.join(SRC, "styles.css"), "utf8"),
  readFile(path.join(SRC, "index.html"), "utf8"),
]);

const stamp = new Date().toISOString().slice(0, 10);
const header = `/* Generated by scripts/sync-resume.mjs from ${SRC} on ${stamp}.
   Do not edit here -- change the source and re-run \`npm run sync-resume\`.

   Selectors are scoped under ${SCOPE} because the source stylesheet was written
   for a standalone page and sets body, p, and h1-h3 globally, while this page
   also carries the site's navigation, footer, and table art. */
`;

const backdrop = `
/* The resume sits over the table-art backdrop rather than on one of its own, so
   the standalone design's desk background, paper background, and page-edge
   shadow are dropped. */
${SCOPE} { background: transparent; }
${SCOPE} .page { background: transparent; box-shadow: none; }
`;

const css = header + scopeBlock(sourceCss, SCOPE) + backdrop;
const page = extractPage(sourceHtml);

const indexPath = path.join(OUT, "index.html");
const existing = await readFile(indexPath, "utf8");
const updated = inject(existing, page);

const pdfSource = await readFile(path.join(SRC, "resume.pdf"));

await writeFile(path.join(OUT, "resume.css"), css);
await writeFile(indexPath, updated);
await writeFile(path.join(OUT, "resume.pdf"), pdfSource);

const kb = (n) => `${(n / 1024).toFixed(1)} KB`;
console.log(`resume synced from ${SRC}`);
console.log(`  resume.css  ${kb(css.length)}`);
console.log(`  resume.pdf  ${kb(pdfSource.length)}`);
console.log(`  markup      ${page.split("\n").length} lines injected into index.html`);
