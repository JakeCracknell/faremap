let fareSelectorFunction = getFareSelectorFunction("default");

$('input[name="routePreferenceRadios"]:radio').change(e => {
    fareSelectorFunction = getFareSelectorFunction(e.target.value);
}); //TODO and redraw


function getFareSelectorFunction(fareSelectorName) {
    if (fareSelectorName === 'cheapest') {
        return fs => Math.min.apply(Math, filterFares(fs).map(function (f) {
            return f.price;
        }));
    } else {
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
