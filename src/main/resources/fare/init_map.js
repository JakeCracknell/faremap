L.mapbox.accessToken = 'pk.eyJ1IjoiemV0dGVyIiwiYSI6ImVvQ3FGVlEifQ.jGp_PWb6xineYqezpSd7wA';

map = L.mapbox.map('map', 'zetter.i73ka9hn', {
    zoomControl: false,
    maxBounds:[[58.62, -5.88], [50.00, 1.76]], //GB
    minZoom: 7,
    maxZoom: 15
}).fitBounds([[51.92, 0.61], [51.11, -1.12]]); //London

voronoiMap(map, '/api/station');