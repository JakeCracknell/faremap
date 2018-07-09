function formatPrice(price) {
    return "£" + (price / 100).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
}

function formatStationName(station) {
    return station.stationName + ((station.crs && " (" + station.crs + ")") || "");
}

function displayFares(fares) {
    const faresContainer = document.getElementById("fares-container");
    faresContainer.innerHTML = fares.map(f => {
        const color = getFillColourForPrice(f.price);
        return "<div class=\"card fare-card shadow-sm my-2\" style='background: linear-gradient(to right, white, " + color + ");'>\n" +
            "  <div class=\"card-body fare-card-body\">\n" +
            "    <div class=\"fare-card-header\">\n" +
            "      <h5 class=\"card-title float-left fare-card-title\">" + getFareTitle(f) + "</h5>\n" +
            "      <h5 class=\"card-title float-right fare-card-price\">" + formatPrice(f.price) + "</h5>\n" +
            "    </div>\n" +
            "    <h6 class=\"card-subtitle float-left text-muted fare-card-description\">" + f.routeDescription + "</h6>\n" +
            "  </div>\n" +
            "</div>\n";
    }).join("\n");
}

function getFareTitle(fare) {
    if (fare.hops !== undefined) {
        return "Split Ticket (" + fare.hops.length + " hops)";
    } else if (fare.isTFL) {
        return "TFL Oyster / Contactless";
    } else {
        return fare.ticketName;
    }
}
