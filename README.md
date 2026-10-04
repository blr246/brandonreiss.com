# brandonreiss.com

Source and build pipeline for <https://brandonreiss.com>.

The live site is a set of static files using **root-absolute paths** (`/style.css`,
`/lib/`, `/graphics/`), so there is no bundler and no framework — the "build" is a
byte-faithful copy of `site/` into `dist/`.

## Quick start

```bash
npm install
npm run dev          # build + watch + live-reload at http://127.0.0.1:8080/
```

Edit anything under `site/`. The watcher rebuilds and the browser reloads.

## Commands

| Command | What it does |
| --- | --- |
| `npm run dev` | Watch `site/` → rebuild `dist/` → live-reload at :8080 |
| `npm run build` | One-shot incremental build into `dist/` |
| `npm run serve` | Static preview of `dist/`, no watching |
| `npm run deploy:dry` | Show what `aws s3 sync` would transfer; touches nothing |
| `npm run deploy` | Sync `dist/` → S3, then invalidate CloudFront |
| `npm run clean` | Remove `dist/` |

## Layout

```
site/       source of truth — committed
dist/       build output — gitignored, regenerated
scripts/    build / dev / serve / proxy / deploy
```

### What is *not* in this repo

Two datasets are intentionally excluded (`.gitignore` plus build exclusions):

- **`site/noweats/data/`** — ~37k objects, ~147 MB
- **`site/private/`** — ~1.2 GB of personal media

`dist/` therefore contains only the **173 files, 3.1 MB** that make up the public site.

## Local proxy for the excluded datasets

`/noweats/data/*` is served by the local dev/preview server **by proxying to the
live site**, so `noweats` works locally without mirroring 37k objects:

```
browser ──HTTP──> 127.0.0.1:8080 ──HTTPS──> brandonreiss.com
```

- Implemented in `scripts/proxy.mjs`, registered as Connect middleware in
  `dev.mjs` and `serve.mjs`.
- **No local TLS certificate is needed.** The browser talks plain HTTP to
  localhost, and Node establishes the outbound TLS connection itself with
  `https.request`, using the system root CA store. `http://127.0.0.1` is already
  a secure context, so nothing in the page is blocked.
- Live-server's *built-in* `--proxy` is **not** used: it hardcodes
  `preserveHost: true` and forwards `Host: 127.0.0.1:8080` to CloudFront, which
  fails the TLS handshake (SSL alert 40 → HTTP 500). The custom middleware sets
  an explicit `Host` and TLS `servername`.
- Response bodies are forwarded **byte-for-byte** — `noweats` objects are stored
  gzip-encoded with `content-encoding: gzip` metadata, so re-encoding would
  corrupt them. Redirects back to the live domain are rewritten to relative
  paths so local browsing never jumps to production.

Verified byte-identical against live for `list.json` and deep paths
(`.counts`, `.interesting`).

To proxy another excluded path, add its prefix to `PROXY_PREFIXES` in
`scripts/proxy.mjs`.

## Deploy

```bash
npm run build
npm run deploy:dry   # review
npm run deploy       # sync + invalidate
```

`scripts/deploy.sh`:

1. Refuses to run unless the caller is account **814447972905**.
2. `aws s3 sync dist/ s3://brandonreiss.com/`
3. Invalidates CloudFront distribution **`EAW3ZQA2585VF`**
   (`brandonreiss.com`, `www.brandonreiss.com`).

Two things it deliberately does **not** do:

- **No `--delete`.** The bucket also holds `noweats/data/` (37k objects) and
  `private/` (1.2 GB), which are absent from `dist/`. A `--delete` run would
  erase them.
- **No origin/website-config changes.** The origin is the S3 *website* endpoint
  (`brandonreiss.com.s3-website-us-east-1.amazonaws.com`), which is what makes
  `/graphics/` resolve to `index.html`. Keep `IndexDocument.suffix = index.html`.

CloudFront caches for ~24h (observed age: 85131s ≈ 23.6h), so **skipping
invalidation means changes stay invisible for up to a day**. Use
`--no-invalidate` only when that is acceptable.

## Initial import

The first commit is a byte-exact import of the live site, verified with
`diff -rq` against the S3 sync directory. Later changes — meta tags, robots,
and other standards work — are separate commits so the history stays legible.
