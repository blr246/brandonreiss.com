# Project instructions

This repository is the source for brandonreiss.com: a static site whose Java
homework applets run in modern browsers through CheerpJ.

## Goals

- Keep the applet pages (Hw01–Hw09 and Hw07a) working in modern browsers.
- Keep every page usable on a phone: nothing may scroll horizontally, at any
  width. This is a hard requirement, not a preference.
- Keep Lighthouse at 100/100/100/100 for `/` and `/resume/`. Those two pages
  are the yardstick; sub-page scores are deliberately not pursued.
- Preserve the site's quirky, homemade, content-focused character. Do not
  restyle it to look like a professionally designed site.

## Rules

### Layout, artwork and applets

- Applet boxes may only shrink, never grow. The `width`/`height` attributes in
  the applet HTML are the maximum size — extra pixels cost software-rasteriser
  performance — and CSS may add `aspect-ratio` or a smaller `max-width` so the
  box fits narrow screens.
- Do not spend effort optimizing the homework applets' rendering performance
  further. Known limits (Hw03's fixed 30ms sleep, Hw07a's thread pool) are
  accepted as they stand.
- Keep the subdivision animation in `site/lib/gentable.js`; it is the site's
  identity. It fits the box before drawing and restarts the drawing whenever
  the box changes: cells must never be moved after they are drawn, because
  that scores as a layout shift.
- The table artwork (`div#table-art`) shrinks to the window with
  `max-width: calc(100% - 2px)`; `html { overflow-x: hidden }` in
  `site/style.css` stays as enforcement for anything wide added later.
- The responsiveness check is structural, not observational: at 412, 375 and
  320 pixels, `document.scrollingElement.scrollWidth` must equal
  `clientWidth` on every page, and neither wheel nor touch gestures may pan
  sideways. "The gesture did nothing" is not sufficient — no content may
  exceed the viewport.

### Content conventions

- Page titles join with ` | `; the active page is marked with an underline
  only; the displayed name is "Brandon Reiss".
- The navigation block is hand-copied into every page; `npm run build` checks
  that all copies match.
- Sub-pages intentionally have no meta description. SEO effort belongs on `/`
  and `/resume/`.
- Java sources keep the name "Brandon L. Reiss".
- jQuery inside `noweats/` and `nanomunchers/` is not to be touched.
  `nanomunchers/` stays bare minimum: its 800px game board scrolls inside
  `#game-screen` rather than being scaled, because the game reads its
  coordinates from that size.

### Comments and commits

- Add a comment only when something notable needs explaining, in plain
  standard wording — no metaphors, no flourishes.
- Code and comments are not a changelog: no dates, no commit references, no
  records of past changes. Say what the code does and why it is shaped that
  way.
- Keep distinct concerns in separate commits. Working notes such as TODO.md
  are never committed — they are gitignored.

### Generated and external files

- `site/resume/` is generated. Never edit it directly: change
  `~/Documents/resume/styles.css` and run `npm run sync-resume`.
- Compiled jars are gitignored; only `.java` sources are committed.
- There is no system-wide JDK; the build caches one at
  `~/.cache/brandonreiss-site/jdk`.
- `~/brandonreiss.com` is a local sync copy of the S3 bucket and is read-only
  from here: the source of truth for byte-exact restores.

### Deployment

- `npm run deploy` runs `scripts/deploy.sh`: S3 sync, cache headers, then a
  CloudFront invalidation.
- The sync must never gain `--delete`. The bucket also holds `noweats/data/`
  and `private/`, which are large mirrored datasets absent from `dist/`
  (and gitignored), so deletion is never part of this workflow.
- The same consequence runs the other way: a page removed from the
  repository keeps being served from S3 until it is removed from the bucket
  by hand.

## Architecture

- `site/` is the source. `npm run build` compiles the applet jars
  (`scripts/build-applets.mjs`) and copies `site/` into `dist/`
  (`scripts/build.mjs`), pruning files deleted from the source. `dist/` and
  jars are gitignored.
- `npm run dev` runs the rebuild watcher and a static server at
  `http://127.0.0.1:8080`; `npm run serve` is the server alone.
- Applets: CheerpJ 4.3 loads `<cheerpj-applet>` elements targeting Java 8.
  They are lazy — `site/lib/applet.js` swaps in the loader on click, so pages
  pay no applet cost until asked. Hw06 is patched at build time by
  `applyYieldPatch()` in `scripts/build-applets.mjs`.
- The background animation is `site/lib/gentable.js` drawing into
  `div#table-art`; its sizing rules live in `site/style.css`.
- `scripts/sync-resume.mjs` regenerates `site/resume/` from
  `~/Documents/resume`.
- `scripts/deploy.sh` syncs `dist/` to `s3://brandonreiss.com` (account
  814447972905, distribution `EAW3ZQA2585VF`), sets content types and cache
  headers via `scripts/set-cache.mjs`, and invalidates CloudFront.
- Verification: Lighthouse against `/` and `/resume/` for the scores, plus a
  browser probe at 412/375/320 for the fit and no-pan checks described above.
