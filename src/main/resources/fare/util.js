function getDistanceFormatted(stationA, stationB) {
    return getDistance(stationA, stationB).toFixed(1) + " km";
}

function getDistance(stationA, stationB) {
    const lon1 = toRadian(stationA.longitude);
    const lat1 = toRadian(stationA.latitude);
    const lon2 = toRadian(stationB.longitude);
    const lat2 = toRadian(stationB.latitude);

    const deltaLat = lat2 - lat1;
    const deltaLon = lon2 - lon1;

    return 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(deltaLat / 2), 2) +
        Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(deltaLon / 2), 2))) * 6371;
}

function toRadian(degree) {
    return degree * Math.PI / 180;
}