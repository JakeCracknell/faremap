let selectedSourceStation;
let selectedDestinationStation;
let pendingSourceStation;
let pendingDestinationStation;

$("#selected-source-station-input").click(resetSourceStation);
$("#selected-destination-station-input").click(resetDestinationStation);

function stationPeek(station) {
    if (!selectedSourceStation) {
        pendingSourceStation = station;
    } else if (!selectedDestinationStation) {
        pendingDestinationStation = station;
    }
    displaySelectedStationsAndFares();
    highlightSourceAndDestination();
}

function stationSelect(station) {
    if (station === selectedSourceStation) {
        selectedSourceStation = null;
        selectedDestinationStation = null;
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
    $('.station-polygon').toggleClass('selectable', !(selectedSourceStation && selectedDestinationStation));
}