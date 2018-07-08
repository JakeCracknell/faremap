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
            "    <div class=\"fare-card-header\">\n" +
            "      <h5 class=\"card-title float-left fare-card-title\">" + fare.routeDescription + "</h5>\n" +
            "      <h5 class=\"card-title float-right fare-card-price\">" + formatPrice(fare.price) + "</h5>\n" +
            "    </div>\n" +
            "    <h6 class=\"card-subtitle float-left text-muted fare-card-description\">" + fare.restrictions + "</h6>\n" +
            "  </div>\n" +
            "</div>\n";
        faresContainer.innerHTML += card;
    });
}