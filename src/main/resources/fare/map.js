let pointsMap = {};
let lastSelectedPoint;

function drawWithLoading(e) {
    d3.select('#loading').classed('visible', true);
    if (e && e.type === 'viewreset') {
        d3.select('#overlay').remove();
    }
    setTimeout(function () {
        draw();
        d3.select('#loading').classed('visible', false);
    }, 0);
}

function draw() {
    d3.select('#overlay').remove();

    let bounds = map.getBounds();
    let topLeft = map.latLngToLayerPoint(bounds.getNorthWest());
    let currentSelectedModes = d3.set(getSelectedCheckboxesFromGroup('#mode-toggles'));

    pointsMap.forEach(translateAndSetCoordinates);

    let drawLimit = bounds.pad(0.4);
    let setOfXYPointsToDraw = d3.set();
    drawableStations = [...pointsMap.values()].filter(function (d) {
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

    maxPriceCurrentlyDisplayed = drawableStations.reduce(function (currentMax, thisPoint) {
        const fare = preferredFareSelectorListFunction(thisPoint.fares)[0]; //Poss to use original func?
        if (fare !== undefined) {
            return Math.max(currentMax, fare.price);
        } else {
            return currentMax;
        }
    }, 0);

    voronoi(drawableStations).forEach(function (d) {
        d.point.cell = d;
    });


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
        .data(drawableStations)
        .enter().append("g")
        .attr("class", "point");

    var buildPathFromPoint = function (point) {
        return "M" + point.cell.join("L") + "Z";
    };

    svgPoints.append("path")
        .attr("class", "point-cell")
        .attr("d", buildPathFromPoint)
        .style('fill', function (d) {
            const fare = preferredFareSelectorFunction(d.fares);
            return fare && getFillColourForPrice(fare.price) || 'transparent';
        })
        .on('click', selectPointForFareQuery)
        .on('mouseover', showMouseOverInformationForPoint)
        .classed("selected", function (d) {
            return lastSelectedPoint === d;
        })
        .classed("nodata", function (d) {
            return d.fares === undefined || d.fares.length === 0;
        });

    svgPoints.append("circle")
        .attr("transform", function (d) {
            return "translate(" + d.x + "," + d.y + ")";
        })
        .style('fill', function (d) {
            return '#' + d.color
        })
        .attr("r", 2);

    d3.selectAll(".route-line").remove();
    drawableStations.forEach(s => s.fares.forEach(f => drawLineBetweenStationsInFare(lastSelectedPoint, f)))
}

var voronoi = d3.geom.voronoi()
    .x(function (d) {
        return d.x;
    })
    .y(function (d) {
        return d.y;
    });


var selectPointForFareQuery = function () {
    d3.selectAll('.selected').classed('selected', false);

    var cell = d3.select(this),
        point = cell.datum();

    lastSelectedPoint = point;
    cell.classed('selected', true);

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
    const point = d3.select(this).datum();
    document.getElementById("selected-source").value = formatStationName(lastSelectedPoint || point);
    document.getElementById("selected-destination").value = formatStationName(point);
    displayFares(filterFaresByTravelTime(point.fares));
};

function drawLineBetweenStationsInFare(startPoint, fare) {
    const stationIds = (fare.hops || []).map(h => h.waypoint);
    const pointsToDraw = [startPoint].concat(stationIds.map(id => pointsMap.get(id)));
    var lineFunction = d3.svg.line()
        .x(function (d) {
            return d.x;
        })
        .y(function (d) {
            return d.y;
        });
    d3.select("#overlay").select("g")
        .append("path")
        .attr("d", lineFunction(pointsToDraw))
        .attr("class", "route-line");
}

//TODO migrate similar
function getTRForFareWithWaypoint(fare) {
    var tr = document.createElement("tr");
    var td = document.createElement("td");
    td.classList.add("fare-type", fare.fareDetail.isTFL ? "tfl" : "nr");
    tr.appendChild(td);
    td = document.createElement("td");
    td.appendChild(document.createTextNode("→ " + formatStationName(pointsMap.get(fare.waypoint))));
    tr.appendChild(td);
    td = document.createElement("td");
    td.appendChild(document.createTextNode(formatPrice(fare.fareDetail.price)));
    tr.appendChild(td);
    td = document.createElement("td");
    td.appendChild(document.createTextNode(fare.fareDetail.routeDescription));
    tr.appendChild(td);
    return tr;
}

function translateAndSetCoordinates(station) {
    station.latlng = new L.LatLng(station.latitude, station.longitude);
    station.xyPoint = map.latLngToLayerPoint(station.latlng);
    station.x = station.xyPoint.x; //TODO remove if poss
    station.y = station.xyPoint.y;
}
