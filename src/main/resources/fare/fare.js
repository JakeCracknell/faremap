let preferredFareSelectorFunction = getPreferredFareSelectorFunction("default", "peak");
let maxPriceCurrentlyDisplayed = 0;

$('input[name="routePreferenceRadios"]:radio, input[name="travelTimeRadios"]:radio').change(e => {
    preferredFareSelectorFunction = getPreferredFareSelectorFunction(
        document.querySelector('input[name="routePreferenceRadios"]:checked').value,
        document.querySelector('input[name="travelTimeRadios"]:checked').value
    );
});

function getPreferredFareSelectorFunction(routePreference, travelTimePreference) {
    return function (fares) {
        fares = filterFaresByTravelTime(fares || [], travelTimePreference);
        return fares.filter(f => routePreference !== 'default' || f.isDefaultRoute)
            .sort((f1, f2) => f1.price - f2.price);
    };
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
        const fare = preferredFareSelectorFunction(thisPoint.fares)[0]; //Poss to use original func?
        if (fare !== undefined) {
            return Math.max(currentMax, fare.price);
        } else {
            return currentMax;
        }
    }, 0);
}

function getFillColourForStation(station) {
    return getFillColourForFare(preferredFareSelectorFunction(station.fares)[0]);
}

function getFillColourForFare(fare) {
    if (fare === undefined || fare.price === 0) {
        return 'transparent';
    }
    const percentage = fare.price / maxPriceCurrentlyDisplayed;
    const hue = (percentage * 360).toString(10);
    return "hsla(" + hue + ",100%,50%,0.5)";
}
