function formatPrice(price) {
    return "£" + (price / 100).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
}

function formatStationName(station) {
    return station.stationName + ((station.crs && " (" + station.crs + ")") || "");
}

function displayFares(fareSet) {
    const faresContainer = document.getElementById("fares-container");
    const topFareCardDiv = getFareCardDiv(fareSet.preferred, getFillColourForFare(fareSet.preferred));
    const otherFareCardDivs = fareSet.valid.map(f => getFareCardDiv(f, 'white')).join("");
    faresContainer.innerHTML = topFareCardDiv + otherFareCardDivs;
    fareSet.splitTicket && populateSplitTicketModal(fareSet.splitTicket);
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

function populateSplitTicketModal(splitTicketFare) {
    let tableHtml = '';
    let lastStation = selectedSourceStation;
    splitTicketFare.hops.forEach(fareHop => {
        const thisStation = pointsMap.get(fareHop.waypoint);
        tableHtml += `<tr><td>${formatStationName(lastStation)}</td>
                          <td>${formatStationName(thisStation)}</td>
                          <td>${getFareTitle(fareHop.fareDetail) + "<br/>" + fareHop.fareDetail.routeDescription}</td>
                          <td>${formatPrice(fareHop.fareDetail.price)}</td>
                          <td>${getDistanceFormatted(lastStation, thisStation)}</td>
                          </tr>`;
        lastStation = thisStation;
    });
    $("#split-ticket-modal-title").text(`Split Ticket from ${formatStationName(selectedSourceStation)} 
                                                to ${formatStationName(selectedDestinationStation)}`);
    $("#split-ticket-modal-tbody").html(tableHtml);
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