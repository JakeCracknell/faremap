let preferredFareSelectorFunction = getPreferredFareSelectorFunction("default");
let maxPriceCurrentlyDisplayed = 0;

$('input[name="routePreferenceRadios"]:radio').change(e => {
    preferredFareSelectorFunction = getPreferredFareSelectorFunction(e.target.value);
});

function getPreferredFareSelectorFunction(fareSelectorName) {
    return fares => (fares || []).filter(f => fareSelectorName !== 'default' || f.isDefaultRoute)
        .sort((f1, f2) => f1.price - f2.price);
}

function setMaxPriceCurrentlyDisplayedFromList(stations) {
    maxPriceCurrentlyDisplayed = stations.reduce(function (currentMax, thisPoint) {
        const fare = preferredFareSelectorFunction(thisPoint.fares)[0]; //Poss to use original func?
        if (fare !== undefined) {
            return Math.max(currentMax, fare.price);
        } else {
            return currentMax;
        }
    }, 0);
}

function filterFaresByTravelTime(fares) {
    if (document.querySelector('input[name="travelTimeRadios"]:checked').value === 'peak') {
        return fares.filter(fare => !fare.offPeakOnly)
    } else {
        const offPeakFares = fares.filter(fare => fare.offPeakOnly);
        const universalFares = fares.filter(fare => !offPeakFares.some(oFare =>
            oFare.ticketType === fare.ticketType && oFare.routeDescription === fare.routeDescription));
        return offPeakFares.concat(universalFares);
    }
}

//TODO inline if only one call site
function getFillColourForStation(station) {
    const fare = preferredFareSelectorFunction(station.fares)[0];
    return fare && getFillColourForPrice(fare.price) || 'transparent';
}

function getFillColourForPrice(price) {
    return getFillColourForPercentage(price / maxPriceCurrentlyDisplayed);
}

function getFillColourForPercentage(percentage) {
    if (percentage === Infinity || percentage === -Infinity || !percentage) return 'transparent';
    var hue = (percentage * 360).toString(10);
    return ["hsla(", hue, ",100%,50%,0.5)"].join("");
}
