function resetUI() {
    document.getElementById("ui-")
}

function formatPrice(price) {
    return "£" + (price / 100).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
}

function formatStationName(station) {
    return station.stationName + ((station.crs && " (" + station.crs + ")") || "");
}

function displayFares(fares) {
    const faresContainer = document.getElementById("fares-container");
    faresContainer.innerHTML = '';

    fares.forEach(fare => {
        const card = "\n" +
            "<div class=\"card fare-card shadow-sm my-2\">\n" +
            "  <div class=\"card-body fare-card-body\">\n" +
            "    <div class=\"fare-card-text\">\n" +
            "      <h5 class=\"card-title fare-card-title\">" + fare.routeDescription + "</h5>\n" +
            "      <h6 class=\"card-subtitle fare-card-description mb-2 text-muted\">" + fare.restrictions + "</h6>\n" +
            "    </div>\n" +
            "    <div class=\"fare-card-price\">" + formatPrice(fare.price) + "</div>\n" +
            "  </div>\n" +
            "</div>\n";
        faresContainer.innerHTML += card;
    });
}