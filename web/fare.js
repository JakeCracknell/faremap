let preferredFareSelectorFunction;
let maxPriceCurrentlyDisplayed = 0;

function setPreferredFareSelectorFunctionFromChecked() {
    preferredFareSelectorFunction = getPreferredFareSelectorFunction(
        document.querySelector('input[name="route-preference-options"]:checked').value,
        document.querySelector('input[name="travel-time-options"]:checked').value
    );
}

function getPreferredFareSelectorFunction(routePreference, travelTimePreference) {
    return function (fares) {
        if (fares === undefined || fares.length === 0) {
            return {valid: [], invalid: [], colour: 'transparent'}
        }
        let fareSet = splitFaresIntoValidAndNonValidForTravelTime(fares, travelTimePreference);
        fareSet.valid = fareSet.valid.sort((f1, f2) => f1.price - f2.price);
        fareSet.splitTicket = fareSet.valid.filter(f => f.hops && f.hops.length > 0)[0];
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
        const universalFares = peakFares.filter(fare => !(fare.hops && fare.hops.length > 0) &&
            !offPeakFares.some(oFare =>
            oFare.ticketName === fare.ticketName && oFare.routeDescription === fare.routeDescription));
        return {valid: offPeakFares.concat(universalFares), invalid: peakFares.filter(f => universalFares.indexOf(f) < 0)};
    }
}

function setMaxPriceCurrentlyDisplayedFromList(stations) {
    maxPriceCurrentlyDisplayed = stations.reduce(function (currentMax, thisPoint) {
        const fare = preferredFareSelectorFunction(thisPoint.fares).preferred; //Poss to use original func?
        if (fare !== undefined && fare.price < 500_00) { // MUF makes this necessary. TODO: fix in atoc code?
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