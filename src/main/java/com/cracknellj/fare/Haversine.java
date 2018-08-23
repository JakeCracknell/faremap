package com.cracknellj.fare;

import com.cracknellj.fare.objects.Station;

public class Haversine {
    private static final int APPROX_EARTH_RADIUS_KM = 6371;

    public static double distance(Station station1, Station station2) {
        return distance(station1.latitude, station1.longitude, station2.latitude, station2.longitude);
    }

    public static double distance(double startLat, double startLong,
                                  double endLat, double endLong) {

        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return APPROX_EARTH_RADIUS_KM * c;
    }

    private static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}