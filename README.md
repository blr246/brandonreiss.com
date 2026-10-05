# brandonreiss.com

Source and build pipeline for <https://brandonreiss.com>.

The live site is a set of static files using **root-absolute paths** (`/style.css`,
`/lib/`, `/graphics/`), so there is no bundler and no framework. The build copies
`site/` into `dist/` byte-for-byte, then generates `sitemap.xml` from the tree
so the sitemap can never go stale.

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
| `npm run deploy` | Sync `dist/` → S3, set cache headers, invalidate CloudFront |
| `npm run set-cache` | Apply `Cache-Control` to every object already in the bucket |
| `npm run sync-resume` | Pull the current resume in from its own repository |
| `npm run clean` | Remove `dist/` |

## Resume

The resume is maintained in its own repository, `~/Documents/resume`, which owns
the copy, the markup, the stylesheet, and the PDF build. It is deliberately not
a git submodule: the markup and stylesheet both have to be rewritten to sit
inside this site, so linking the repo would not remove the transformation step —
it would only add submodule bookkeeping to every clone. To read from somewhere
else:

```sh
RESUME_SRC=/path/to/resume npm run sync-resume
```

That regenerates three committed files:

| File | What it is |
| --- | --- |
| `site/resume/resume.pdf` | byte-for-byte copy |
| `site/resume/index.html` | the `.page` markup, injected between `<!-- resume:begin -->` and `<!-- resume:end -->` |
| `site/resume/resume.css` | every selector re-scoped under `.resume` |

Scoping is required because the source stylesheet was written for a standalone
page: it sets `body`, `p`, and `h1-h3` globally, while `/resume/` also carries
the site's navigation, footer, and table art. `:root` (design tokens) and
`@page` (print geometry) pass through untouched.

Edit the resume in its own repository, then re-run the command. The outputs are
committed, so the site still builds without that checkout present.

## Layout

```
site/       source of truth — committed
dist/       build output — gitignored, regenerated
scripts/    build / dev / serve / proxy / deploy / set-cache
```

### What is *not* in this repo

Two datasets are intentionally excluded (`.gitignore` plus build exclusions):

- **`site/noweats/data/`** — ~37k objects, ~147 MB
- **`site/private/`** — ~1.2 GB of personal media

`dist/` therefore contains only the **173 files, 3.1 MB** that make up the public
site, plus the generated `sitemap.xml`.

## Standards

Every HTML file now carries `<!doctype html>`, `<meta charset="utf-8">`, a
`viewport` meta, `<html lang="en">`, and a favicon link. The six standalone
content pages additionally carry a `meta description`, a `rel=canonical`
pointing at `https://brandonreiss.com`, and Open Graph tags.

`robots.txt` keeps its original `Disallow: /lib/` and now advertises the
generated sitemap.

Deliberately **not** tagged for SEO: `resume/resume.html` (injected into
`resume/index.html` via `$.get`, so its `<head>` is discarded) and the bare
`hw0X.html` applet stubs.

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
npm run deploy       # sync + cache headers + invalidate
```

`scripts/deploy.sh`:

1. Refuses to run unless the caller is account **814447972905**.
2. `aws s3 sync dist/ s3://brandonreiss.com/`
3. Runs `scripts/set-cache.mjs` to apply `Cache-Control` to every object.
4. Invalidates CloudFront distribution **`EAW3ZQA2585VF`**
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

### Cache headers

`aws s3 sync` only uploads files whose bytes changed, so on its own it never
updates metadata for an object that is otherwise up to date. `set-cache.mjs`
closes that gap by walking `dist/` and rewriting `Cache-Control` on each
object:

| Kind | Policy | Why |
| --- | --- | --- |
| `*.html` (26 files) | `max-age=3600` | Cheap to revalidate; keeps returning visitors within an hour of a deploy |
| everything else (148 files) | `max-age=86400` | Changes rarely, and CloudFront is invalidated on deploy |

There is no asset fingerprinting (filenames are not hashed), so anything
longer than a day would serve returning visitors stale CSS. CloudFront
invalidation clears the *edge* but not a visitor's *browser* cache — these
headers are what govern the latter.

The script reads each object's existing `Content-Type` back from S3 instead of
guessing it from the file extension, so MIME types are preserved exactly
(`.js` files have drifted between `application/javascript` and
`text/javascript` across past deploys). It only ever lists files present under
`dist/`, so `noweats/data/` and `private/` are never touched.

Verify with:

```bash
curl -sSI https://brandonreiss.com/ | grep -i cache-control
curl -sSI https://brandonreiss.com/style.css | grep -i cache-control
```

## Initial import

The first commit is a byte-exact import of the live site, verified with
`diff -rq` against the S3 sync directory. Later changes — meta tags, robots,
and other standards work — are separate commits so the history stays legible.
