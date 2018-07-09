let preferredFareSelectorFunction = getPreferredFareSelectorFunction("default");

$('input[name="routePreferenceRadios"]:radio').change(e => {
    preferredFareSelectorFunction = getPreferredFareSelectorFunction(e.target.value);
}); //TODO and redraw


function getPreferredFareSelectorFunction(fareSelectorName) {
    if (fareSelectorName === 'cheapest') {
        return fs => Math.min.apply(Math, filterFares(fs).map(function (f) {
            return f.price;
        }));
    } else { //default
        return fs => Math.min.apply(Math, filterFares(fs).filter(f => f.isDefaultRoute).map(function (f) {
            return f.price;
        }));
    }
}

function filterFares(fares) {
    if (document.querySelector('input[name="travelTimeRadios"]:checked').value === 'peak') {
        return fares.filter(fare => !fare.offPeakOnly)
    } else {
        const offPeakFares = fares.filter(fare => fare.offPeakOnly);
        const universalFares = fares.filter(fare => !offPeakFares.some(oFare =>
            oFare.ticketType === fare.ticketType && oFare.routeDescription === fare.routeDescription));
        return offPeakFares.concat(universalFares);
    }
}
