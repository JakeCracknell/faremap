const map = L.map('map', {
    minZoom: 6,
    maxZoom: 15,
    maxBounds: new L.LatLngBounds(new L.LatLng(58.62, -5.88), new L.LatLng(50.00, 1.76))
}).setView([51.5, -0.6], 9);

map.doubleClickZoom.disable();

const osmAttrib='Map data © <a href="https://openstreetmap.org">OpenStreetMap</a> contributors';
L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {opacity: 0.5, attribution: osmAttrib}).addTo(map);

$("input[name*='-options']:radio").change(e => {
    preferredFareSelectorFunction = getPreferredFareSelectorFunction(
        document.querySelector('input[name="route-preference-options"]:checked').value,
        document.querySelector('input[name="travel-time-options"]:checked').value
    );
    drawMap();
});

$("#selected-source").find(".station-deselect-button").click(resetSourceStation);
$("#selected-destination").find(".station-deselect-button").click(resetDestinationStation);

function hidePolygonsWithPriceGreaterThan(price) {
    d3.select("#map-svg-overlay").select("g").selectAll("g")
        .style("opacity", s => (s.fareSet.preferred && s.fareSet.preferred.price <= price) ? 1 : 0);
}

function showPriceForColorKeyHover(e) {
    const price = Math.max(0, maxPriceCurrentlyDisplayed * (e.offsetX / e.target.clientWidth));
    $("#color-key")
        .attr('data-original-title', formatPrice(price))
        .tooltip('show');
    hidePolygonsWithPriceGreaterThan(price);
}

$("#color-key").mousemove(showPriceForColorKeyHover);

$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});

loadStationsAsync();
