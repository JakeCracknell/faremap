//TODO add a loading indicator
function drawWithLoading(e) {
    if (e && e.type === 'viewreset') {
        d3.select('#map-svg-overlay').remove();
    }
    setTimeout(() => draw(), 0);
}

function draw() {
    d3.select('#map-svg-overlay').remove();
    stationsByIdMap.forEach(translateAndSetCoordinates);
    const drawableStations = getDrawableStationsAsList();
    setMaxPriceCurrentlyDisplayedFromList(drawableStations);
    createVoronoiPolygons(drawableStations);
    stationsByIdMap.forEach(initialiseFareSets);
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
        .style("margin-top", topLeft.y + "px")
        .append("g")
        .attr("transform", "translate(" + (-topLeft.x) + "," + (-topLeft.y) + ")")
        .selectAll("g")
        .data(drawableStations)
        .enter().append("g");

    svg.append("path")
        .attr("class", "station-polygon")
        .attr("d", s => "M" + s.polygon.join("L") + "Z")
        .style('fill', s => s.fareSet.colour)
        .on('click', stationSelect)
        .on('mouseover', stationPeek);

    svg.append("circle")
        .attr("transform", s => "translate(" + s.x + "," + s.y + ")")
        .attr("r", 2)
        .attr("class", "station-point");

    setSelectableStatusOnStationPolygons();
    drawSelectedRouteLine();
    displaySelectedStationsAndFares();
}

function getDrawableStationsAsList() {
    let currentSelectedModes = d3.set(getSelectedCheckboxesFromGroup('#mode-toggles'));
    let drawLimit = map.getBounds().pad(0.4);
    let setOfXYPointsToDraw = d3.set();
    return [...stationsByIdMap.values()].filter(function (d) {
        if (!(d.modes.some(m => currentSelectedModes.has(m)) && drawLimit.contains(d.latlng))) {
            return false;
        }

        // filters points that are right on top of each other, of which there are 135 (e.g. Hammersmith, Amersham Rail/Tube)
        // display messed up if this happens. TODO Long term solution is to change backend to group such stations
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
            stationsByIdMap.get(toStationId).fares = json.fares[toStationId];
        }
        drawWithLoading();
    });
}

function translateAndSetCoordinates(station) {
    station.latlng = new L.LatLng(station.latitude, station.longitude);
    station.xyPoint = map.latLngToLayerPoint(station.latlng);
    station.x = station.xyPoint.x;
    station.y = station.xyPoint.y;
}
