const routeLineFunction = d3.svg.line().x(d => d.x).y(d => d.y);

function getStationsInFare(fare, destination) {
    if (fare && fare.hops) {
        return fare.hops.map(hop => stationsByIdMap.get(hop.waypoint)).unshift(selectedDestinationStation);
    } else {
        return [selectedSourceStation, destination];
    }
}

function drawSelectedRouteLine() {
    d3.selectAll(".selected-route-line").remove();
    if (selectedSourceStation) {
        const destination = selectedDestinationStation || pendingDestinationStation;
        if (destination) {
            const stations = getStationsInFare(destination.fareSet.preferred, destination);
            d3.select("#map-svg-overlay").select("g")
                .append("path")
                .attr("d", dest => routeLineFunction(stations))
                .attr("class", "route-line selected-route-line");
        }
    }
}

function drawSplitTicketTree() {
    d3.selectAll(".split-ticket-tree").remove();
    d3.select("#map-svg-overlay").select("g").selectAll("g")
        .filter(dest => dest.fareSet.splitTicket)
        .append("path")
        .attr("class", "route-line split-ticket-tree")
        .attr("d", dest => routeLineFunction([selectedSourceStation].concat(dest.fareSet.splitTicket.hops.map(hop => stationsByIdMap.get(hop.waypoint)))));
}
