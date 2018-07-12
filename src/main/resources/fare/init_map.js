L.mapbox.accessToken = 'pk.eyJ1IjoiemV0dGVyIiwiYSI6ImVvQ3FGVlEifQ.jGp_PWb6xineYqezpSd7wA';

const map = L.mapbox.map('map', 'zetter.i73ka9hn', {
    zoomControl: false,
    maxBounds:[[58.62, -5.88], [50.00, 1.76]], //GB
    minZoom: 7,
    maxZoom: 15
}).fitBounds([[51.92, 0.61], [51.11, -1.12]]); //London

map.doubleClickZoom.disable();

map.on('ready', function () {
    d3.json('/api/station', function (json) {
        pointsMap = new Map(json.map((p) => [p.stationId, p]));
        $('input[name="routePreferenceRadios"]:radio, input[name="travelTimeRadios"]:radio').change(drawWithLoading);
        map.addLayer({
            onAdd: function (map) {
                map.on('viewreset moveend', drawWithLoading);
                drawWithLoading();
            }
        });
    })
});