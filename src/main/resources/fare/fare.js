let preferredFareSelectorFunction = getPreferredFareSelectorFunction("default");
let preferredFareSelectorListFunction = getPreferredFareSelectorListFunction("default");
let maxPriceCurrentlyDisplayed = 0;

$('input[name="routePreferenceRadios"]:radio').change(e => {
    preferredFareSelectorFunction = getPreferredFareSelectorFunction(e.target.value);
    preferredFareSelectorListFunction = getPreferredFareSelectorListFunction(e.target.value);
});

function setMaxPriceCurrentlyDisplayedFromList(stations) {
    maxPriceCurrentlyDisplayed = stations.reduce(function (currentMax, thisPoint) {
        const fare = preferredFareSelectorListFunction(thisPoint.fares)[0]; //Poss to use original func?
        if (fare !== undefined) {
            return Math.max(currentMax, fare.price);
        } else {
            return currentMax;
        }
    }, 0);
}

//TODO choose one of the below and refactor the other
function getPreferredFareSelectorFunction(fareSelectorName) {
    return fares => (fares || []).filter(f => fareSelectorName !== 'default' || f.isDefaultRoute)
        .sort((f1, f2) => f1.price - f2.price)[0];
}

function getPreferredFareSelectorListFunction(fareSelectorName) {
    return fares => (fares || []).filter(f => fareSelectorName !== 'default' || f.isDefaultRoute)
        .sort((f1, f2) => f1.price - f2.price);
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

function getFillColourForPrice(price) {
    return getFillColourForPercentage(price / maxPriceCurrentlyDisplayed);
}

function getFillColourForPercentage(percentage) {
    if (percentage === Infinity || percentage === -Infinity || !percentage) return 'transparent';
    var hue = (percentage * 360).toString(10);
    return ["hsla(", hue, ",100%,50%,0.5)"].join("");
}
