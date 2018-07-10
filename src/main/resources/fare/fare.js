let preferredFareSelectorFunction = getPreferredFareSelectorFunction("default");
let preferredFareSelectorListFunction = getPreferredFareSelectorListFunction("default");
let maxPriceCurrentlyDisplayed = 0;

$('input[name="routePreferenceRadios"]:radio').change(e => {
    preferredFareSelectorFunction = getPreferredFareSelectorFunction(e.target.value);
    preferredFareSelectorListFunction = getPreferredFareSelectorListFunction(e.target.value);
});

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