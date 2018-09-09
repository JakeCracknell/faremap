L.mapbox.accessToken = 'pk.eyJ1IjoiZHJzcGE0NCIsImEiOiJjamo5MWloNDYwNHZ6M2txeGVrMWJxc3ppIn0.RibkexMCj1fRzadpmTdgFw';

const map = L.mapbox.map('map', 'mapbox.streets-basic', {
    zoomControl: false,
    maxBounds:[[58.62, -5.88], [50.00, 1.76]], //GB
    minZoom: 6,
    maxZoom: 15
}).fitBounds([[51.92, 0.61], [51.11, -1.12]]); //London

map.doubleClickZoom.disable();

map.on('ready', function () {
    d3.json('./data/stations.json', function (stationList) {
        stationsByIdMap = new Map(stationList.map((p) => [p.stationId, p]));
        initialiseTypeAhead(stationList);
        map.addLayer({
            onAdd: function (map) {
                map.on('viewreset', removeSvgLayer);
                map.on('moveend', drawWithLoading); // on zoom, fires viewreset, then moveend.      rezie=moveend
                drawWithLoading();
            }
        });
    })
});

$('input[name="routePreferenceRadios"]:radio, input[name="travelTimeRadios"]:radio').change(e => {
    preferredFareSelectorFunction = getPreferredFareSelectorFunction(
        document.querySelector('input[name="routePreferenceRadios"]:checked').value,
        document.querySelector('input[name="travelTimeRadios"]:checked').value
    );
    drawWithLoading(e);
});

$("#selected-source").find(".station-deselect-button").click(resetSourceStation);
$("#selected-destination").find(".station-deselect-button").click(resetDestinationStation);

function showPriceForColorKeyHover(e) {
    $("#color-key")
        .attr('data-original-title', formatPrice(Math.max(0,
            maxPriceCurrentlyDisplayed * (e.offsetX / e.target.clientWidth))))
        .tooltip('show');
}

$("#color-key").mousemove(showPriceForColorKeyHover);

$(function () {
    $('[data-toggle="tooltip"]').tooltip()
})