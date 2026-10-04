/*
 * noweats.js -- Library for noweats application.
 */

////////////////////////////////////////////////////////////////////////////////
// Functions
////////////////////////////////////////

var getDataList = function(cb) {
  //$.ajax({
  //    url:"http://brandonreiss.com.s3-website-us-east-1.amazonaws.com/noweats/data/list.json",
  //    dataType: "jsonp",
  //    jsonp: false,
  //    jsonpCallback: function() {},
  //    succsss: cb
  //});
  //$.getJSON("http://brandonreiss.com.s3-website-us-east-1.amazonaws.com/noweats/data/list.json", cb);
  $.ajax({
      dataType: "jsonp",
      url:"/noweats/data/list.json",
      jsonp: false,
      jsonpCallback: "callback",
      success: cb
  });
};

var loadData = function(pathPrefix) {
  $.getJSON(
      pathPrefix + '.counts',
      function(data) {

        // Clear the table body.
        $.tablesorter.clearTableBody(table[0]);

        // Load the data.
        for (var food in data) {
          tbody.append("<tr><td class=\"food\">" + food
            + "</td><td class=\"count\">"
            + data[food] + "</td></tr>")
        }

        table.trigger("update");

      });

  $.getJSON(
      pathPrefix + '.interesting',
      function(data) {

        var div = $("div#interesting")

        // Clear the table body.
        div.html("")

        // Load the data.
        for (var food in data) {
          div.append("<blockquote>" + data[food] + "</blockquote>")
        }
      });

  // Taken from
  // http://stackoverflow.com/questions/11887934/
  // check-if-daylight-saving-time-is-in-effect-and-if-it-is-for-how-many-hours 
  Date.prototype.stdTimezoneOffset = function() {
    var jan = new Date(this.getFullYear(), 0, 1);
    var jul = new Date(this.getFullYear(), 6, 1);
    return Math.max(jan.getTimezoneOffset(), jul.getTimezoneOffset());
  }
  Date.prototype.dst = function() {
    return this.getTimezoneOffset() < this.stdTimezoneOffset();
  }

  var pathParts = pathPrefix.split(".");
  var date = pathParts[pathParts.length - 1];
  var dateParts = date.split("_");
  var yyMMDD = dateParts[0].split("-");
  var hour = parseInt(dateParts[1]);
  var theDate = new Date(yyMMDD[0], yyMMDD[1] - 1, yyMMDD[2], hour + 1);
  // Check daylight savings.
  if (theDate.dst()) {
    theDate = new Date(theDate.getTime() + 3600000);
  }
  $("div#last-updated").html("<p>" + theDate.toLocaleString() + " EST" + "</p>");
};
