const map = L.map('map', {
    minZoom: 6, maxZoom: 15,
    maxBounds: new L.LatLngBounds(new L.LatLng(58.62, -5.88), new L.LatLng(50.00, 1.76))
}).setView([51.5, -0.6], 9);

map.doubleClickZoom.disable();

L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
    {opacity: 0.5, attribution: "Map data © <a href='https://openstreetmap.org'>OpenStreetMap</a> contributors"})
    .addTo(map);

$("input[name*='-options']:radio").change(e => {
    setPreferredFareSelectorFunctionFromChecked();
    drawMap();
});

$("#selected-source").find(".station-deselect-button").click(resetSourceStation);
$("#selected-destination").find(".station-deselect-button").click(resetDestinationStation);

setPreferredFareSelectorFunctionFromChecked();
initialiseColorKey();
loadStationsAsync();
