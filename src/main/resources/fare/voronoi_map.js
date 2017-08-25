addShowHideEventsTo = function(selector) {
  d3.select(selector).select('.hide').on('click', function(){
    d3.select(selector)
      .classed('visible', false)
      .classed('hidden', true);
  });

  d3.select(selector).select('.show').on('click', function(){
    d3.select(selector)
      .classed('visible', true)
      .classed('hidden', false);
  });
}

voronoiMap = function(map, url) {
  var pointModes = d3.map(),
      points = [],
      pointsMap = {},
      lastSelectedPoint;

  var voronoi = d3.geom.voronoi()
      .x(function(d) { return d.x; })
      .y(function(d) { return d.y; });

  var selectPointForFareQuery = function() {
    d3.selectAll('.selected').classed('selected', false);

    var cell = d3.select(this),
        point = cell.datum();

    lastSelectedPoint = point;
    cell.classed('selected', true);

    d3.select('#selected h1')
      .html('')
      .append('p')
      .text(point.stationId + " " + point.stationName)

    fareUrl = "/api/fare/from/" + point.stationId
    d3.json(fareUrl, function(json) {
      pointsMap.forEach(function(station, stationId, m) {
        station.fares = []
      });
      for (var toStationId in json.fares) {
        pointsMap.get(toStationId).fares = json.fares[toStationId];
      }
      drawWithLoading();
    })
  }

  var showMouseOverInformationForPoint = function() {
      var cell = d3.select(this),
          point = cell.datum();

      d3.select('#selected h1')
        .html('')
        .append('p')
        .text(point.stationId + " " + point.stationName)
        .append('ul').selectAll('li')
        .data(point.fares)
        .enter()
        .append('li')
        .html(f => JSON.stringify(f));
    }

  var setupDisplayOptionsPanel = function() {
    addShowHideEventsTo('#selections')
    d3.selectAll('#mode-toggles, #fare-type-toggles')
        .on("change", drawWithLoading);
  }

  var selectedModes = function() {
    return d3.selectAll('#mode-toggles input[type=checkbox]')[0].filter(function(elem) {
      return elem.checked;
    }).map(function(elem) {
      return elem.value;
    })
  }

  var pointsFilteredToSelectedModes = function() {
    var currentSelectedModes = d3.set(selectedModes());
    return points.filter(function(item){
      return item.modes.some(m => currentSelectedModes.has(m));
    });
  }

  var drawWithLoading = function(e){
    d3.select('#loading').classed('visible', true);
    if (e && e.type == 'viewreset') {
      d3.select('#overlay').remove();
    }
    setTimeout(function(){
      draw();
      d3.select('#loading').classed('visible', false);
    }, 0);
  }

  var draw = function() {
    d3.select('#overlay').remove();

    var bounds = map.getBounds(),
        topLeft = map.latLngToLayerPoint(bounds.getNorthWest()),
        bottomRight = map.latLngToLayerPoint(bounds.getSouthEast()),
        existing = d3.set(),
        drawLimit = bounds.pad(0.4);

    filteredPoints = pointsFilteredToSelectedModes().filter(function(d) {
      var latlng = new L.LatLng(d.latitude, d.longitude);

      if (!drawLimit.contains(latlng)) { return false };

      var point = map.latLngToLayerPoint(latlng);

      key = point.toString();
      if (existing.has(key)) { return false };
      existing.add(key);

      d.x = point.x;
      d.y = point.y;
      return true;
    });

    voronoi(filteredPoints).forEach(function(d) { d.point.cell = d; });

    var svg = d3.select(map.getPanes().overlayPane).append("svg")
      .attr('id', 'overlay')
      .attr("class", "leaflet-zoom-hide")
      .style("width", map.getSize().x + 'px')
      .style("height", map.getSize().y + 'px')
      .style("margin-left", topLeft.x + "px")
      .style("margin-top", topLeft.y + "px");

    var g = svg.append("g")
      .attr("transform", "translate(" + (-topLeft.x) + "," + (-topLeft.y) + ")");

    var svgPoints = g.attr("class", "points")
      .selectAll("g")
        .data(filteredPoints)
      .enter().append("g")
        .attr("class", "point");

    var buildPathFromPoint = function(point) {
      return "M" + point.cell.join("L") + "Z";
    }

    var getFillColourForAdjustedPrice = function(price) {
      if (price == Infinity || !price) return 'transparent';
      var hue=((1-price)*120).toString(10);
      return ["hsl(",hue,",100%,50%)"].join("");
    }

    var fareSelectorElement = document.getElementById('fare-price-selector');
    var fareSelectorFunction;
    switch (fareSelectorElement.options[fareSelectorElement.selectedIndex].id) {
        case 'low':
            fareSelectorFunction = fs => Math.min.apply(Math, fs.map(function(f){return f.price;}))
            break;
        case 'high':
            fareSelectorFunction = fs => Math.max.apply(Math, fs.map(function(f){return f.price;}))
            break;
        default:
            fareSelectorFunction = fs => Math.min.apply(Math, fs.filter(d => d.isDefaultRoute).map(function(f){return f.price;}))
            break;
    }
    var averagePrice = 5.0
    svgPoints.append("path")
      .attr("class", "point-cell")
      .attr("d", buildPathFromPoint)
      .style('fill', function(d) { return getFillColourForAdjustedPrice(fareSelectorFunction(d.fares) / averagePrice) })
      .on('click', selectPointForFareQuery)
      .on('mouseover', showMouseOverInformationForPoint)
      .classed("selected", function(d) { return lastSelectedPoint == d} );

    svgPoints.append("circle")
      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
      .style('fill', function(d) { return '#' + d.color } )
      .attr("r", 2);
  }

  var mapLayer = {
    onAdd: function(map) {
      map.on('viewreset moveend', drawWithLoading);
      drawWithLoading();
    }
  };

  addShowHideEventsTo('#about');

  map.on('ready', function() {
    d3.json(url, function(json) {
      points = json;
      points.forEach(function(point) {
        point.modes.forEach(m => pointModes.set(m, {mode: m, color: 'black'}));
        point.fares = [];
      })
      pointsMap = new Map(points.map((p) => [p.stationId, p]));
      setupDisplayOptionsPanel();
      map.addLayer(mapLayer);
    })
  });
}