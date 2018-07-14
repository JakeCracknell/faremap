function formatPrice(price) {
    return "£" + (price / 100).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
}

function formatStationName(station) {
    return station.stationName + ((station.crs && " (" + station.crs + ")") || "");
}

function displaySelectedStationsAndFares() {
    displayStationsAndFares(selectedSourceStation || pendingSourceStation,
        selectedDestinationStation || pendingDestinationStation);
}

function displayStationsAndFares(sourceStation, destinationStation) {
    $("#selected-source-station-input").val(sourceStation && formatStationName(sourceStation) || "");
    $("#selected-destination-station-input").val(destinationStation && formatStationName(destinationStation) || "");
    if (destinationStation) {
        displayFares(destinationStation.fareSet);
    }
}

function displayFares(fareSet) {
    const faresContainer = document.getElementById("fares-container");
    const preferredFareCardDiv = (fareSet.preferred && getFareCardDiv(fareSet.preferred, fareSet.colour)) || "";
    const alternativeFareCardDivs = fareSet.valid.map(f => getFareCardDiv(f, 'white')).join("");
    faresContainer.innerHTML = preferredFareCardDiv + alternativeFareCardDivs;
    fareSet.splitTicket && populateSplitTicketModal(fareSet.splitTicket);
}

function getFareCardDiv(fare, colour) {
    const modalProperties = fare.hops && "href='#' data-toggle='modal' data-target='#split-ticket-modal'" || "";
    return `<div class="card fare-card shadow-sm my-2"
                 style="background: linear-gradient(to right, white, ${colour});">
              <div class="card-body fare-card-body">
                <div class="fare-card-header">
                  <h5 class="card-title float-left fare-card-title">${getFareTitle(fare)}</h5>
                  <h5 class="card-title float-right fare-card-price">${formatPrice(fare.price)}</h5>
                </div>
                <a class="card-subtitle text-muted h6 fare-card-description" ${modalProperties}>${fare.routeDescription}</a>
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
    $("#split-ticket-modal-title").text(`Split Ticket from ${formatStationName(selectedSourceStation)} to 
                                        ${formatStationName(selectedDestinationStation || pendingDestinationStation)}`);
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