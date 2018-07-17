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
    setTypeAheadField(station.formattedName);
    displaySelectedStationsAndFares();
    drawSelectedRouteLine();
}

function stationSelect(station) {
    if (station === selectedSourceStation) {
        resetSourceStation();
    } else if (!selectedSourceStation) {
        selectedSourceStation = station;
        setTypeAheadField("");
        $("#selected-source-text").text(selectedSourceStation.formattedName).show();
        $("#selected-source-header, #pending-destination-header").show();
        $("#pending-source-header").hide();
        triggerFareRequest();
    } else if (!selectedDestinationStation) {
        selectedDestinationStation = station;
        $("#selected-destination-text").text(selectedDestinationStation.formattedName).show();
        $("#selected-destination-header").show();
        $("#pending-destination-header, #pending-station-picker-div").hide();
        setTypeAheadField("");
    }
    setSelectableStatusOnStationPolygons();
}

function resetSourceStation() {
    selectedSourceStation = null;
    pendingSourceStation = null;
    resetDestinationStation();
    setTypeAheadField("");
    $("#selected-source-header, #selected-source-text, #pending-destination-header").hide();
    $("#pending-source-header, #pending-station-picker-div").show();
}

function resetDestinationStation() {
    selectedDestinationStation = null;
    pendingDestinationStation = null;
    displaySelectedStationsAndFares();
    setSelectableStatusOnStationPolygons();
    $("#selected-destination-header, #selected-destination-text").hide();
    $("#pending-destination-header, #pending-station-picker-div").show();
}

function setSelectableStatusOnStationPolygons() {
    $('.station-polygon, #map').toggleClass('selectable', !(selectedSourceStation && selectedDestinationStation));
}

function onTypeAheadStationSelect(e, station) {
    stationSelect(station);
}

function setTypeAheadField(value) {
    $(".typeahead").typeahead("val", value).typeahead("close");
}
