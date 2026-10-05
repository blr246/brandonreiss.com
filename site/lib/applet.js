// Loads CheerpJ on request rather than on page load. The runtime is a whole
// JVM in WebAssembly -- megabytes -- so pulling it in unprompted would make
// every reader pay for something they may never click, and would put its
// startup work on the main thread while the page is still settling.

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

for (const button of document.querySelectorAll(".applet-run")) {
  button.addEventListener("click", () => {
    button.disabled = true;
    button.textContent = "Starting\u2026";
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
