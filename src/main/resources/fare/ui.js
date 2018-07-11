function formatPrice(price) {
    return "£" + (price / 100).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
}

function formatStationName(station) {
    return station.stationName + ((station.crs && " (" + station.crs + ")") || "");
}

function displayFares(fares) {
    const faresContainer = document.getElementById("fares-container");
    const topFare = preferredFareSelectorFunction(fares);
    faresContainer.innerHTML = getFareCardDiv(topFare, getFillColourForPrice(topFare.price)) +
        fares.filter(f => f !== topFare).sort((f1, f2) => f1.price - f2.price)
            .map(f => getFareCardDiv(f, 'white')).join("");
    populateSplitTicketModal(fares.filter(f => f.hops !== undefined && f.hops.length > 0)[0]);
}

function getFareCardDiv(fare, colour) {
    return `<div class="card fare-card shadow-sm my-2"
                 data-toggle="modal" data-target="#split-ticket-modal"
                 style="background: linear-gradient(to right, white, ${colour});">
              <div class="card-body fare-card-body">
                <div class="fare-card-header">
                  <h5 class="card-title float-left fare-card-title">${getFareTitle(fare)}</h5>
                  <h5 class="card-title float-right fare-card-price">${formatPrice(fare.price)}</h5>
                </div>
                <h6 class="card-subtitle float-left text-muted fare-card-description">${fare.routeDescription}</h6>
              </div>
            </div>`
}

function populateSplitTicketModal(fare) {
    $("#split-ticket-modal-title").text(getFareTitle(fare));
    $("#split-ticket-modal-body").html( //TODO properly
        `<div class="card fare-card shadow-sm my-2">
              <div class="card-body fare-card-body">
                <div class="fare-card-header">
                  <h5 class="card-title float-left fare-card-title">${getFareTitle(fare)}</h5>
                  <h5 class="card-title float-right fare-card-price">${formatPrice(fare.price)}</h5>
                </div>
                <h6 class="card-subtitle float-left text-muted fare-card-description">${fare.routeDescription}</h6>
              </div>
            </div>`
    );
    // var tr = document.createElement("tr");
    // var td = document.createElement("td");
    // td.classList.add("fare-type", fare.fareDetail.isTFL ? "tfl" : "nr");
    // tr.appendChild(td);
    // td = document.createElement("td");
    // td.appendChild(document.createTextNode("→ " + formatStationName(pointsMap.get(fare.waypoint))));
    // tr.appendChild(td);
    // td = document.createElement("td");
    // td.appendChild(document.createTextNode(formatPrice(fare.fareDetail.price)));
    // tr.appendChild(td);
    // td = document.createElement("td");
    // td.appendChild(document.createTextNode(fare.fareDetail.routeDescription));
    // tr.appendChild(td);
    // return tr;
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



//TODO migrate properly
var getSelectedCheckboxesFromGroup = function (selector) {
    checkedInputs = document.querySelectorAll(selector + ' input[type=checkbox]:checked');
    return [].slice.call(checkedInputs).map(function (c) {
        return c.value;
    });
};