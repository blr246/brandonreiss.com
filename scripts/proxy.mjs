/**
 * Connect middleware: proxy a path prefix to the live site.
 *
 * Why not live-server's built-in `--proxy`? It hardcodes `preserveHost: true`,
 * so it forwards `Host: 127.0.0.1:8080` to CloudFront, which fails the TLS
 * handshake (SSL alert 40) and returns HTTP 500. This middleware sets an
 * explicit Host and TLS servername instead.
 *
 * Design notes:
 *  - Body bytes are forwarded VERBATIM. `noweats/data/*.json` is served with
 *    `content-encoding: gzip` (the object is stored gzipped), so decompressing
 *    or re-encoding would corrupt it. We never touch the payload.
 *  - Redirects pointing back at the live domain are rewritten to relative
 *    paths so local browsing does not silently jump to production.
 *
 * Only data that is too large to mirror is proxied; everything else builds
 * from site/ locally.
 */

import * as httpsModule from "node:https";

const LIVE_HOST = "brandonreiss.com";
/** Path prefixes to proxy. Extend this list if more datasets are excluded. */
export const PROXY_PREFIXES = ["/noweats/data/"];

const HOP_BY_HOP = new Set([
  "connection",
  "keep-alive",
  "proxy-authenticate",
  "proxy-authorization",
  "te",
  "trailers",
  "transfer-encoding",
  "upgrade",
]);

function stripHopByHop(headers) {
  const out = {};
  for (const [key, value] of Object.entries(headers)) {
    if (HOP_BY_HOP.has(key.toLowerCase())) continue;
    out[key] = value;
  }
  return out;
}

/**
 * @returns {import("connect").NextHandleFunction}
 */
export function createLiveProxy(prefixes = PROXY_PREFIXES) {
  return function liveProxy(req, res, next) {
    const matched = prefixes.find((p) => req.url.startsWith(p));
    if (!matched) return next();

    const headers = stripHopByHop(req.headers);
    // CloudFront routes on Host; without this the handshake fails.
    headers.host = LIVE_HOST;
    headers["x-forwarded-host"] = req.headers.host || "localhost";
    headers["x-forwarded-proto"] = "https";

    const request = httpsModule.request(
      {
        protocol: "https:",
        hostname: LIVE_HOST,
        port: 443,
        servername: LIVE_HOST, // explicit SNI
        method: req.method,
        path: req.url,
        headers,
      },
      (upstream) => {
        const outHeaders = stripHopByHop(upstream.headers);

        // Keep local browsing on localhost.
        const location = outHeaders.location;
        if (location) {
          try {
            const url = new URL(location, `https://${LIVE_HOST}`);
            if (url.hostname === LIVE_HOST) {
              outHeaders.location = url.pathname + url.search + url.hash;
            }
          } catch {
            /* leave unparseable Location alone */
          }
        }

        res.writeHead(upstream.statusCode || 502, outHeaders);
        upstream.pipe(res);
      },
    );

    request.on("error", (err) => {
      // next() would let the static handler serve a misleading 404 instead of
      // revealing that the proxy itself broke.
      res.writeHead(502, { "content-type": "text/plain; charset=utf-8" });
      res.end(`proxy error: could not reach ${LIVE_HOST}: ${err.message}\n`);
    });

    // Pipe the local request body through for non-GET methods.
    req.pipe(request);
  };
}
