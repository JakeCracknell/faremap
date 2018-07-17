L.mapbox.accessToken = 'pk.eyJ1IjoiemV0dGVyIiwiYSI6ImVvQ3FGVlEifQ.jGp_PWb6xineYqezpSd7wA';

const map = L.mapbox.map('map', 'mapbox.streets-basic', {
    zoomControl: false,
    maxBounds:[[58.62, -5.88], [50.00, 1.76]], //GB
    minZoom: 7,
    maxZoom: 15
}).fitBounds([[51.92, 0.61], [51.11, -1.12]]); //London

map.doubleClickZoom.disable();

map.on('ready', function () {
    d3.json('/api/station', function (stationList) {
        stationsByIdMap = new Map(stationList.map((p) => [p.stationId, p]));
        initialiseTypeAhead(stationList);
        map.addLayer({
            onAdd: function (map) {
                map.on('viewreset moveend', drawWithLoading);
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
