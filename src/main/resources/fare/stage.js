let selectedSourceStation;
let selectedDestinationStation;
let pendingSourceStation;
let pendingDestinationStation;

$("#selected-source-station-input").click(() => selectedSourceStation = null);
$("#selected-destination-station-input").click(() => selectedDestinationStation = null);

function stationPeek(station) {
    if (!selectedSourceStation) {
        pendingSourceStation = station;
    } else if (!selectedDestinationStation) {
        pendingDestinationStation = station;
    }
    displayStationsAndFares(selectedSourceStation || pendingSourceStation, selectedDestinationStation || pendingDestinationStation);
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
}