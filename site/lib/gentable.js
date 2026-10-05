// Table art: recursively subdivides a rectangle into ~600 random cells, forever.
TableGenerator = function() {

  var DIVS = 600;
  var TABLE = document.getElementById("table-art");
  var LEAVES = null;
  var MINAREA = 1.0 / DIVS;
  var NUMCELLS = 0;
  var appendPartition = null;
  var COMPLETE_PAUSE = 10000;
  var SPLIT_PAUSE = 60;
  var TB_PATTERN = ['<div class="tg-t" style="height:P%"></div>',
                    '<div class="tg-b" style="height:P%"></div>'];
  var LR_PATTERN = ['<div class="tg-l" style="width:P%"></div>',
                    '<div class="tg-r" style="width:P%"></div>'];
  var ASPECT = null;

  // Preallocate index array (to sort later).
  var INDICES = [];
  for (var i = 0; i < DIVS; ++i) {
    INDICES.push(i);
  }

  var partitionHelper = function(ele, pattern, partition) {
    var parentArea = ele._area;
    ele.insertAdjacentHTML("beforeend", pattern[0].replace("P", partition.toFixed(2)));
    ele.insertAdjacentHTML("beforeend", pattern[1].replace("P", (100 - partition).toFixed(2)));
    var appended = ele.children;
    appended[0]._area = parentArea *        partition  * 0.01;
    appended[1]._area = parentArea * (100 - partition) * 0.01;
  };

  // Append random partition and allow split on cells with sufficient area.
  var appendRandMinarea = function(ele) {
    var minPartition = 10;
    var partition = minPartition
      + Math.floor(Math.random() * (101 - (2 * minPartition)));
    if (Math.random() >= (0.50 * ASPECT)) {
      partitionHelper(ele, LR_PATTERN, partition);
    } else {
      partitionHelper(ele, TB_PATTERN, partition);
    }
  }

  // Split 10-90 with uniform probability over direction.
  var appendSplitFixed = function() {
    // Range in [5, 40].
    var partition = 5 + (Math.random() * 36);
    return function(ele) {
      if (Math.random() >= (0.50 * ASPECT)) {
        partitionHelper(ele, LR_PATTERN, partition);
      } else {
        partitionHelper(ele, TB_PATTERN, partition);
      }
    };
  }

  // Start table dividing over again.
  var resetTable = function() {
    var selectStyle = Math.random();
    //if (selectStyle >= 0.5) {
    if (true) {
      appendPartition = appendSplitFixed();
    } else {
      appendPartition = appendRandMinarea;
    }

    ASPECT = TABLE.clientWidth / TABLE.clientHeight;

    TABLE.innerHTML = "";
    TABLE._area = 1.0;
    appendPartition(TABLE);
    LEAVES = Array.prototype.slice.call(TABLE.children);
    NUMCELLS = 2;
    setTimeout(splitCell, 0);
  }

  // Split one cell.
  var splitCell = function() {

    // Sort indices descending by area.
    var indices = INDICES.slice(0, LEAVES.length);
    for (var i = 0; i < LEAVES.length; ++i) {
      indices[i] = i;
    }
    indices.sort(function(a, b) {
      var aArea = LEAVES[a]._area;
      var bArea = LEAVES[b]._area;
      if (bArea > aArea) {
        return 1;
      } else if (bArea < aArea) {
        return -1;
      } else {
        return 0;
      }
    });

    var rand = Math.random();
    var splitIdx = indices[Math.floor(LEAVES.length * rand * rand)];
    var toSplit = LEAVES[splitIdx];
    appendPartition(toSplit);

    // Don't make too small cells eligible for splitting.
    var kids = Array.prototype.slice.call(toSplit.children);
    var toAdd = kids.filter(function(el) {
      return el._area > MINAREA;
    });

    // The children of the cell just split form a contiguous run starting at
    // splitIdx, so swapping them in for it keeps LEAVES in document order.
    LEAVES.splice(splitIdx, 1, ...toAdd);

    if (++NUMCELLS >= DIVS) {
      setTimeout(resetTable, COMPLETE_PAUSE);
    } else {
      setTimeout(splitCell, SPLIT_PAUSE);
    }
  };

  return {
     onWindowChanged: function() {
      var bodyHeight = document.body.clientHeight;
      var tableArtDefaultHeight = TABLE.clientHeight;
      var tableArtHeight = Math.max(bodyHeight + 25, tableArtDefaultHeight) + "px";
      TABLE.style.height = tableArtHeight;
    },
    start: function() {
      var that = this;
      resetTable();

      // Recompute once the document is parsed, and again once images settle.
      window.addEventListener("load", function() {
        that.onWindowChanged();
      });

      document.addEventListener("DOMContentLoaded", function() {
        that.onWindowChanged();
      });
    },
  };
}();

TableGenerator.start();
