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
        $("#selected-source-text").text(selectedSourceStation.formattedName);
        $("#selected-source, #pending-destination-header").show();
        $("#pending-source-header").hide();
        triggerFareRequest();
    } else if (!selectedDestinationStation) {
        selectedDestinationStation = station;
        $("#selected-destination-text").text(selectedDestinationStation.formattedName);
        $("#selected-destination").show();
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
    $("#selected-source, #pending-destination-header").hide();
    $("#pending-source-header, #pending-station-picker-div").show();
}

function resetDestinationStation() {
    selectedDestinationStation = null;
    pendingDestinationStation = null;
    displaySelectedStationsAndFares();
    setSelectableStatusOnStationPolygons();
    $("#selected-destination").hide();
    $("#pending-destination-header, #pending-station-picker-div").show();
}

function setSelectableStatusOnStationPolygons() {
    $('.station-polygon, #map').toggleClass('selectable', !(selectedSourceStation && selectedDestinationStation));
}

function initialiseTypeAhead(stationList) {
    stationList.forEach(s => s.formattedName = formatStationName(s));
    const bloodhound = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.nonword('formattedName'),
        queryTokenizer: Bloodhound.tokenizers.nonword,
        local: stationList,
        identify: s => s.stationId
    });
    $("#station-picker-input").typeahead(
        {highlight: true},
        {source: bloodhound.ttAdapter(), displayKey: 'formattedName', name: 'station'})
        .bind('typeahead:select', onTypeAheadStationSelect)
        .focus(clearTypeAheadIfNoMenu).click(clearTypeAheadIfNoMenu);

    bloodhound.initialize();
}

function onTypeAheadStationSelect(e, station) {
    stationSelect(station);
}

function setTypeAheadField(value) {
    $(".typeahead").typeahead("val", value).typeahead("close");
}

//Not perfect, but good enough. First onfocus event will bring up menu then this event will fire
function clearTypeAheadIfNoMenu() {
    if ($('#pending-station-picker-div').find('.tt-menu').is(":hidden")) {
        setTypeAheadField("");
    }
}