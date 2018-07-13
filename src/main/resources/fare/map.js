let pointsMap = {};

//TODO add a loading indicator
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
    const topLeft = map.latLngToLayerPoint(map.getBounds().getNorthWest());
    const svg = d3.select(map.getPanes().overlayPane).append("svg")
        .attr('id', 'map-svg-overlay')
        .attr("class", "leaflet-zoom-hide")
        .style("width", map.getSize().x + 'px')
        .style("height", map.getSize().y + 'px')
        .style("margin-left", topLeft.x + "px")
        .style("margin-top", topLeft.y + "px");

    const svgPoints = svg.append("g")
        .attr("transform", "translate(" + (-topLeft.x) + "," + (-topLeft.y) + ")")
        .attr("class", "points")
        .selectAll("g")
        .data(drawableStations)
        .enter().append("g");

    svgPoints.append("path")
        .attr("class", "station-polygon")
        .attr("d", s => "M" + s.polygon.join("L") + "Z")
        .style('fill', s => s.fareSet.colour)
        .on('click', onStationPolygonClick)
        .on('mouseover', onStationPolygonMouseOver)
        .classed("selected-source-station-polygon", s => selectedSourceStation === s)
        .classed("selected-destination-station-polygon", s => selectedDestinationStation === s);

    svgPoints.append("circle")
        .attr("transform", s => "translate(" + s.x + "," + s.y + ")")
        .attr("class", "station-point");

    drawSplitTicketSpiderMapOnSvgOverlay(drawableStations);
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

function triggerFareRequest() {
    fareUrl = "/api/fare/from/" + selectedSourceStation.stationId;
    d3.json(fareUrl, function (json) {
        for (const toStationId in json.fares) {
            pointsMap.get(toStationId).fares = json.fares[toStationId];
        }
        drawWithLoading();
    });
}

function onStationPolygonClick() {
    stationSelect(d3.select(this).datum());
}

function onStationPolygonMouseOver() {
    stationPeek(d3.select(this).datum());
}

function drawSplitTicketSpiderMapOnSvgOverlay(drawableStations) {
    d3.selectAll(".route-line").remove();
    if (selectedSourceStation) {
        drawableStations.forEach(s => (s.fares || []).forEach(f => drawLineBetweenStationsInFare(selectedSourceStation, f)));
    }
}

function drawLineBetweenStationsInFare(startPoint, fare) {
    const stationIds = (fare.hops || []).map(h => h.waypoint);
    const pointsToDraw = [startPoint].concat(stationIds.map(id => pointsMap.get(id)));
    const lineFunction = d3.svg.line().x(d => d.x).y(d => d.y);
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
