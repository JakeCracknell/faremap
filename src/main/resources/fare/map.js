let pointsMap = {};
let selectedSourceStation;
let selectedDestinationStation;

function drawWithLoading(e) {
    if (e && e.type === 'viewreset') {
        d3.select('#map-svg-overlay').remove();
    }
    setTimeout(function () {
        draw();
    }, 0);
}

function draw() {
    d3.select('#map-svg-overlay').remove();
    pointsMap.forEach(translateAndSetCoordinates);
    const drawableStations = getDrawableStationsAsList();
    setMaxPriceCurrentlyDisplayedFromList(drawableStations);
    createVoronoiPolygons(drawableStations);
    pointsMap.forEach(initialiseFareSets);
    drawSvgOverlay(drawableStations);
}

function drawSvgOverlay(drawableStations) {
    const svg = d3.select(map.getPanes().overlayPane).append("svg")
        .attr('id', 'map-svg-overlay')
        .attr("class", "leaflet-zoom-hide")
        .style("width", map.getSize().x + 'px')
        .style("height", map.getSize().y + 'px');

    const svgPoints = svg.append("g").attr("class", "points")
        .selectAll("g")
        .data(drawableStations)
        .enter().append("g")
        .attr("class", "point");

    svgPoints.append("path")
        .attr("class", "station-polygon")
        .attr("d", station => "M" + station.polygon.join("L") + "Z")
        .style('fill', station => station.fareSet.colour)
        .on('click', selectPointForFareQuery)
        .on('mouseover', showMouseOverInformationForPoint)
        .classed("selected-source-station-polygon", d => selectedSourceStation === d);

    svgPoints.append("circle")
        .attr("transform", d => "translate(" + d.x + "," + d.y + ")")
        .style('fill', d => '#' + d.color)
        .attr("r", 2);

    d3.selectAll(".route-line").remove();
    drawableStations.forEach(s => (s.fares || []).forEach(f => drawLineBetweenStationsInFare(selectedSourceStation, f)));
}

function getDrawableStationsAsList() {
    let currentSelectedModes = d3.set(getSelectedCheckboxesFromGroup('#mode-toggles'));
    let drawLimit = map.getBounds().pad(0.4);
    let setOfXYPointsToDraw = d3.set();
    return [...pointsMap.values()].filter(function (d) {
        if (!(d.modes.some(m => currentSelectedModes.has(m)) && drawLimit.contains(d.latlng))) {
            return false;
        }

        // filters points that are right on top of each other, of which there are 135 (e.g. Hammersmith, Amersham Rail/Tube)
        // display messed up if this happens. Long term solution is to change backend to group such stations
        if (setOfXYPointsToDraw.has(d.xyPoint.toString())) {
            return false;
        }
        setOfXYPointsToDraw.add(d.xyPoint.toString());

        return true;
    });
}

function initialiseFareSets(station) {
    station.fareSet = preferredFareSelectorFunction(station.fares);
}

function createVoronoiPolygons(drawableStations) {
    const voronoiFunction = d3.geom.voronoi().x(d => d.x).y(d => d.y);
    voronoiFunction(drawableStations).forEach(d => d.point.polygon = d);  // d.point is the station object.
}


var selectPointForFareQuery = function () {
    d3.selectAll('.selected').classed('selected', false);

    var polygon = d3.select(this),
        point = polygon.datum();

    selectedSourceStation = point;
    polygon.classed('selected', true);

    fareUrl = "/api/fare/from/" + point.stationId;
    d3.json(fareUrl, function (json) {
        pointsMap.forEach(function (station, stationId, m) {
            station.fares = []
        });
        for (var toStationId in json.fares) {
            pointsMap.get(toStationId).fares = json.fares[toStationId];
        }
        drawWithLoading();
    })
};

var showMouseOverInformationForPoint = function () {
    selectedDestinationStation = d3.select(this).datum();
    document.getElementById("selected-source-station-input").value = formatStationName(selectedSourceStation || selectedDestinationStation);
    document.getElementById("selected-destination-station-input").value = formatStationName(selectedDestinationStation);
    displayFares(preferredFareSelectorFunction(selectedDestinationStation.fares));
};

function drawLineBetweenStationsInFare(startPoint, fare) {
    const stationIds = (fare.hops || []).map(h => h.waypoint);
    const pointsToDraw = [startPoint].concat(stationIds.map(id => pointsMap.get(id)));
    var lineFunction = d3.svg.line().x(d => d.x).y(d => d.y);
    d3.select("#map-svg-overlay").select("g")
        .append("path")
        .attr("d", lineFunction(pointsToDraw))
        .attr("class", "route-line");
}
function translateAndSetCoordinates(station) {
    station.latlng = new L.LatLng(station.latitude, station.longitude);
    station.xyPoint = map.latLngToLayerPoint(station.latlng);
    station.x = station.xyPoint.x;
    station.y = station.xyPoint.y;
}
