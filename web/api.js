let lastFareAjaxRequest;

function loadStationsAsync() {
    resetProgressBarAndShowText("Loading stations data...");
    d3.json('./data/stations.json', onStationsLoaded)
        .on("progress", showAjaxProgressOnProgressBar);
}

function onStationsLoaded(stationList) {
    stationsByIdMap = new Map(stationList.map((p) => [p.stationId, p]));
    initialiseTypeAhead(stationList);
    map.on('viewreset zoomstart', removeSvgLayer); // otherwise the old overlay will be visible for a short time after zoom.
    map.on('moveend', drawMap); // on zoom, fires viewreset, then moveend.
    drawMap();
    hideProgressBar()
}

function loadFaresAsync() {
    gtag('event', 'FareSetRequest', {'event_label' : selectedSourceStation.stationName});
    resetProgressBarAndShowText("Loading fare data for " + selectedSourceStation.stationId + "...");
    const url = `./data/fares/${selectedSourceStation.stationId}.json.gz`;
    //XMLHttpRequest is used instead of d3.json because d3.json does not support gzip.
    lastFareAjaxRequest = new XMLHttpRequest();
    lastFareAjaxRequest.open("GET", url, true);
    lastFareAjaxRequest.responseType = "arraybuffer";
    lastFareAjaxRequest.onload = onFaresLoaded;
    lastFareAjaxRequest.onprogress = showAjaxProgressOnProgressBar;
    lastFareAjaxRequest.send();
}

function onFaresLoaded() {
    const fareJson = JSON.parse(pako.inflate(lastFareAjaxRequest.response, {to: 'string'}));
    faresList = (fareJson && fareJson.fares) || {};
    stationsByIdMap.forEach((station, stationId) => station.fares = faresList[stationId]);
    drawMap();
    hideProgressBar()
}

function showAjaxProgressOnProgressBar(ajaxProgressEvent) {
    const loaded = (d3.event && d3.event.loaded) || ajaxProgressEvent.loaded;
    const total = (d3.event && d3.event.total) || ajaxProgressEvent.total;
    $("#progress-container").show();
    $("#progress-bar").css("width", 100 * (loaded / total) + "%");
    $("#progress-footer").text(`${Math.round(loaded / 1024)} / ${Math.round(total / 1024)} KB`);
}

function resetProgressBarAndShowText(text) {
    $("#progress-bar").css("width", 0 + "%");
    $("#progress-header").text(text);
    $("#progress-footer").text();
}

function hideProgressBar() {
    $("#progress-container").hide();
}

function abortAnyFareAjaxRequests() {
    lastFareAjaxRequest && lastFareAjaxRequest.abort();
    hideProgressBar();
}
