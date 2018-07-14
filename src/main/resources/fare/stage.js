let stationsByIdMap = {};
let selectedSourceStation;
let selectedDestinationStation;
let pendingSourceStation;
let pendingDestinationStation;

function stationPeek(station) {
    if (!selectedSourceStation) {
        pendingSourceStation = station;
    } else if (!selectedDestinationStation) {
        pendingDestinationStation = station;
    }
    displaySelectedStationsAndFares();
    drawSelectedRouteLine();
}

function stationSelect(station) {
    if (station === selectedSourceStation) {
        resetSourceStation();
    } else if (!selectedSourceStation) {
        selectedSourceStation = station;
        triggerFareRequest();
    } else if (!selectedDestinationStation) {
        selectedDestinationStation = station;
    }
    setSelectableStatusOnStationPolygons();
}

function resetSourceStation() {
    selectedSourceStation = null;
    pendingSourceStation = null;
    resetDestinationStation();
}

function resetDestinationStation() {
    selectedDestinationStation = null;
    pendingDestinationStation = null;
    displaySelectedStationsAndFares();
    setSelectableStatusOnStationPolygons();
}

function setSelectableStatusOnStationPolygons() {
    $('.station-polygon, #map').toggleClass('selectable', !(selectedSourceStation && selectedDestinationStation));
}