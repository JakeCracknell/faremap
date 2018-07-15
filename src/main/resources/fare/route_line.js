const routeLineFunction = d3.svg.line().x(d => d.x).y(d => d.y);

function getStationsInFare(fare, destination) {
    if (fare && fare.hops) {
        return [selectedSourceStation].concat(fare.hops.map(hop => stationsByIdMap.get(hop.waypoint)));
    } else {
        return [selectedSourceStation, destination];
    }
}

function drawSelectedRouteLine() {
    d3.selectAll(".selected-route-line").remove();
    if (selectedSourceStation) {
        const destination = selectedDestinationStation || pendingDestinationStation;
        if (destination) {
            d3.select("#map-svg-overlay").select("g")
                .append("path")
                .attr("class", "route-line selected-route-line")
                .attr("d", dest => routeLineFunction(getStationsInFare(destination.fareSet.preferred, destination)));
        }
    }
}

function drawSplitTicketTree() {
    d3.selectAll(".split-ticket-tree").remove();
    d3.select("#map-svg-overlay").select("g").selectAll("g")
        .filter(dest => dest.fareSet.splitTicket)
        .append("path")
        .attr("class", "route-line split-ticket-tree")
        .attr("d", dest => routeLineFunction(getStationsInFare(dest.fareSet.splitTicket, dest)))
        .attr("stroke-dasharray", 1000 + " " + 1000)
        .attr("stroke-dashoffset", 1000)
        .transition().duration(2000)
        .attr("stroke-dashoffset", 0);

}
