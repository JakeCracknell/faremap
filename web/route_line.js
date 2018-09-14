const routeLineFunction = d3.svg.line().x(d => d.x).y(d => d.y);

function getStationsInFare(fare, destination) {
    if (fare && fare.hops) {
        return [selectedSourceStation].concat(fare.hops.map(hop => stationsByIdMap.get(hop.waypoint)));
    } else {
        return [selectedSourceStation, destination];
    }
}

function getStationPairsInSplitTicketFare(station) {
    if (station && station.fareSet && station.fareSet.splitTicket) {
        let hops = station.fareSet.splitTicket.hops.map(hop => stationsByIdMap.get(hop.waypoint));
        let pairs = [[selectedSourceStation, hops[0]]];
        for (let i = 0; i < hops.length - 1; i++) {
            pairs.push(hops.slice(i, i + 2));
        }
        return pairs;
    } else {
        return [];
    }
}

//TODO: probably should not destroy whole svg every zoom/map-move. When fixed, clean up the below.
function drawSelectedRouteLine() {
    const svgLine = !d3.select(".selected-route-line").empty() ? d3.select(".selected-route-line") :
        d3.select("#map-svg-overlay").select("g").append("path").attr("class", "route-line selected-route-line");
    if (selectedSourceStation) {
        const destination = selectedDestinationStation || pendingDestinationStation;
        if (destination) {
            svgLine.transition().ease("elastic").duration(500)
                .attr("d", dest => routeLineFunction(getStationsInFare(destination.fareSet.preferred, destination)));
        }
    }
}

function drawOptionalOverlay() {
    switch (document.querySelector('input[name="point-display-options"]:checked').value) {
        case 'stations':
            return drawStationsOverlay();
        case 'tree':
            return drawSplitTicketTree();
    }
}

function drawSplitTicketTree() {
    d3.select("#map-svg-overlay")
        .select("g")
        .selectAll(".split-ticket-tree")
        .data(getHopsToDrawOnSplitTicketTree())
        .enter()
        .append("path")
        .attr("class", "route-line split-ticket-tree")
        .attr("d", d => d.key)
        .style("stroke-width", d => Math.max(Math.min(10, d.value / 100), 1))
        .style("opacity", d => d.value / 10);
    d3.selectAll(".station-point").remove();
}

function getHopsToDrawOnSplitTicketTree() {
    const hopsBySplitTicket = [...stationsByIdMap.values()].map(getStationPairsInSplitTicketFare);
    const allHops = [].concat.apply([], hopsBySplitTicket);
    const linesAndCounts = allHops.map(routeLineFunction).reduce((result, line) => {
        if (!result.hasOwnProperty(line)) {
            result[line] = 0;
        }
        result[line]++;
        return result;
    }, {});
    return d3.entries(linesAndCounts);
}

function drawStationsOverlay() {
    d3.select("#map-svg-overlay")
        .select("g")
        .selectAll("g")
        .append("circle")
        .attr("transform", s => "translate(" + s.x + "," + s.y + ")")
        .attr("r", getRadiusForStationBlob)
        .attr("class", "station-blob");
}

function getRadiusForStationBlob(station) {
    if (station.yearlyUsage < 250000) {
        return 1;
    } else if (station.yearlyUsage < 500000) {
        return 2;
    } else if (station.yearlyUsage < 2000000) {
        return 3;
    } else if (station.yearlyUsage < 5000000) {
        return 4;
    } else {
        return 5;
    }
}