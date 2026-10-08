// Loads CheerpJ when the button is pressed rather than on page load. The
// runtime is a whole JVM in WebAssembly -- megabytes -- so loading it on every
// page would make each reader download it for a demo they may never click, and
// would run its start-up work on the main thread before the page has finished
// loading.

const CHEERPJ_LOADER = "https://cjrtnc.leaningtech.com/4.3/loader.js";

let loading = null;

function loadCheerpJ() {
  if (loading) return loading;

  loading = new Promise((resolve, reject) => {
    const script = document.createElement("script");
    script.src = CHEERPJ_LOADER;
    script.onload = () => {
      if (typeof window.cheerpjInit !== "function") {
        reject(new Error("the CheerpJ loader did not provide cheerpjInit()"));
        return;
      }
      // Chained either way: resolve() adopts cheerpjInit()'s promise if it
      // returns one, and settles immediately if it does not.
      resolve(window.cheerpjInit());
    };
    script.onerror = () => reject(new Error("the CheerpJ runtime could not be loaded"));
    document.head.appendChild(script);
  });

  return loading;
}

// CheerpJ stops painting if the applet's box changes size after the runtime
// has started: the canvas stays white and, on a phone, the page stops
// responding. Turning the phone while a demo runs changes the box, so the run
// begins by giving the box a final size. It is cut down to the smaller window
// dimension on the way, so the same box still fits after the phone is turned.
function pinAppletBox(button) {
  const host = button.closest(".applet-host");
  if (!host) return;
  const doc = document.documentElement;
  const rect = host.getBoundingClientRect();
  const fit = Math.min(doc.clientWidth, doc.clientHeight) - rect.left;
  const width = Math.max(1, Math.min(host.offsetWidth, fit));
  host.style.boxSizing = "border-box";
  host.style.width = width + "px";
  host.style.maxWidth = "none";
}

for (const button of document.querySelectorAll(".applet-run")) {
  button.addEventListener("click", () => {
    button.disabled = true;
    button.textContent = "Starting\u2026";
    pinAppletBox(button);
    loadCheerpJ().then(
      () => button.remove(),
      (error) => {
        button.disabled = false;
        button.textContent = "Could not start the applet";
        console.error(error);
      },
    );
  });
}
