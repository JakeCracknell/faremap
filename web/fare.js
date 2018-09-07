let preferredFareSelectorFunction = getPreferredFareSelectorFunction("cheapest", "peak");
let maxPriceCurrentlyDisplayed = 0;

function getPreferredFareSelectorFunction(routePreference, travelTimePreference) {
    return function (fares) {
        if (fares === undefined || fares.length === 0) {
            return {valid: [], invalid: [], colour: 'transparent'}
        }
        let fareSet = splitFaresIntoValidAndNonValidForTravelTime(fares, travelTimePreference);
        fareSet.valid = fareSet.valid.sort((f1, f2) => f1.price - f2.price);
        fareSet.splitTicket = fareSet.valid.filter(f => f.hops !== undefined && f.hops.length > 0)[0];
        fareSet.preferred = fareSet.valid.filter(f => routePreference !== 'default' || f.isDefaultRoute)[0];
        fareSet.valid.splice(fareSet.valid.indexOf(fareSet.preferred), 1);
        fareSet.colour = getFillColourForFare(fareSet.preferred);
        return fareSet;
    };
}

function splitFaresIntoValidAndNonValidForTravelTime(fares, travelTimePreference) {
    let offPeakFares = fares.filter(fare => fare.offPeakOnly);
    let peakFares = fares.filter(fare => !fare.offPeakOnly);
    if (travelTimePreference === 'peak') {
        return {valid: peakFares, invalid: offPeakFares}
    } else { // valid = fares marked as off-peak only and anytime fares with no off-peak equivalent.
        const universalFares = peakFares.filter(fare => !offPeakFares.some(oFare =>
            oFare.ticketType === fare.ticketType && oFare.routeDescription === fare.routeDescription));
        return {valid: offPeakFares.concat(universalFares), invalid: peakFares.filter(f => universalFares.indexOf(f) < 0)};
    }
}

function filterFaresByTravelTime(fares, travelTimePreference) {
    if (travelTimePreference === 'peak') {
        return fares.filter(fare => !fare.offPeakOnly)
    } else {
        const offPeakFares = fares.filter(fare => fare.offPeakOnly);
        const universalFares = fares.filter(fare => !offPeakFares.some(oFare =>
            oFare.ticketType === fare.ticketType && oFare.routeDescription === fare.routeDescription));
        return offPeakFares.concat(universalFares);
    }
}

function setMaxPriceCurrentlyDisplayedFromList(stations) {
    maxPriceCurrentlyDisplayed = stations.reduce(function (currentMax, thisPoint) {
        const fare = preferredFareSelectorFunction(thisPoint.fares).preferred; //Poss to use original func?
        if (fare !== undefined) {
            return Math.max(currentMax, fare.price);
        } else {
            return currentMax;
        }
    }, 0);
}

function getFillColourForFare(fare) {
    if (fare === undefined || fare.price === 0) {
        return 'transparent';
    }
    const percentage = fare.price / maxPriceCurrentlyDisplayed;
    const hue = (percentage * 360).toString(10);
    return "hsla(" + hue + ",100%,50%,0.5)";
}

function triggerFareRequest() {
    d3.json(`./data/fares/${selectedSourceStation.stationId}.json`, loadFaresJson);
}

function loadFaresJson(fareJson) {
    faresList = (fareJson && fareJson.fares) || {};
    stationsByIdMap.forEach((station, stationId) => station.fares = faresList[stationId]);
    drawWithLoading();
}

function showColorKeyIfFaresExist() {
    $('#color-key')[maxPriceCurrentlyDisplayed > 0 ? 'fadeIn' : 'fadeOut']();
}