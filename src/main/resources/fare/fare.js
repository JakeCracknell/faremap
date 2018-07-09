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