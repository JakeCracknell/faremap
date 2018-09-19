function loadStationsAsync() {
    d3.json('./data/stations.json', onStationsLoaded)
        .on("progress", () => {
            $("#progress-container").show();
            $("#progress-bar").css("width", 100 * (d3.event.loaded / d3.event.total) + "%");
        });
}

function onStationsLoaded(stationList) {
    stationsByIdMap = new Map(stationList.map((p) => [p.stationId, p]));
    initialiseTypeAhead(stationList);
    map.on('viewreset zoomstart', removeSvgLayer); // otherwise the old overlay will be visible for a short time after zoom.
    map.on('moveend', drawWithLoading); // on zoom, fires viewreset, then moveend.
    drawWithLoading();
    $("#progress-container").hide();
}

function triggerFareRequest() {
    gtag('event', 'FareSetRequest', {'event_label' : selectedSourceStation.stationName});
    $("#progress-bar").css("width", 0 + "%");
    d3.json(`./data/fares/${selectedSourceStation.stationId}.json`, onFaresLoaded)
        .on("progress", () => {
            $("#progress-container").show();
            $("#progress-bar").css("width", 100 * (d3.event.loaded / d3.event.total) + "%");
        });
}

function onFaresLoaded(fareJson) {
    faresList = (fareJson && fareJson.fares) || {};
    stationsByIdMap.forEach((station, stationId) => station.fares = faresList[stationId]);
    drawWithLoading();
    $("#progress-container").hide();
}