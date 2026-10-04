TableGenerator = function() {

  var DIVS = 600;
  var TABLE = $("#table-art");
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
    var parentArea = ele.data("area");
    ele.append(pattern[0].replace("P", partition.toFixed(2)));
    ele.append(pattern[1].replace("P", (100 - partition).toFixed(2)));
    var appended = ele.children("div")
    appended.eq(0).data("area", parentArea *        partition  * 0.01);
    appended.eq(1).data("area", parentArea * (100 - partition) * 0.01);
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

    ASPECT = TABLE.width() / TABLE.height();

    TABLE.html("");
    TABLE.data("area", 1.0);
    appendPartition(TABLE);
    LEAVES = TABLE.children("div");
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
      var aArea = LEAVES.eq(a).data("area");
      var bArea = LEAVES.eq(b).data("area");
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
    var toSplit = LEAVES.eq(splitIdx);
    appendPartition(toSplit);

    // Don't make too small cells eligible for splitting.
    var toAdd = toSplit.children("div").filter(function(idx) {
      return $(this).data("area") > MINAREA;
    });
    LEAVES = LEAVES.add(toAdd);

    LEAVES = LEAVES.not(":eq(" + splitIdx + ")");
    if (++NUMCELLS >= DIVS) {
      setTimeout(resetTable, COMPLETE_PAUSE);
    } else {
      setTimeout(splitCell, SPLIT_PAUSE);
    }
  };

  return {
     onWindowChanged: function() {
      var bodyHeight = parseInt($("body").height());
      var tableArtDefaultHeight = parseInt($("#table-art").height());
      var tableArtHeight = Math.max(bodyHeight + 25, tableArtDefaultHeight) + "px";
      $("#table-art").height(tableArtHeight);
    },
    start: function() {
      var that = this;
      resetTable();

      $(window).load(function() {
        that.onWindowChanged();
      });

      $(document).ready(function() {
        that.onWindowChanged();
      });
    },
  };
}();

TableGenerator.start();
