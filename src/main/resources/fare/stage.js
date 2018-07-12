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
    displaySelectedStations(selectedSourceStation || pendingSourceStation, selectedDestinationStation || pendingDestinationStation);
    displayFares((selectedDestinationStation || pendingDestinationStation).fareSet);
}

function stationSelect(station) {
    if (!selectedSourceStation) {
        selectedSourceStation = station;
        triggerFareRequest();
    } else if (!selectedDestinationStation) {
        selectedDestinationStation = station;
    }
}