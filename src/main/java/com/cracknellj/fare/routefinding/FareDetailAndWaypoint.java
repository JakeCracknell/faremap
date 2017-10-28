package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;

import java.math.BigDecimal;

public class FareDetailAndWaypoint {
    public String waypoint;
    public FareDetail fareDetail;
    public double cumulativeCost;

    public FareDetailAndWaypoint(String waypoint, FareDetail fareDetail, double cumulativeCost) {
        this.waypoint = waypoint;
        this.fareDetail = fareDetail;
        this.cumulativeCost = cumulativeCost;
    }

    public static FareDetailAndWaypoint startNode(String waypoint) {
        return new FareDetailAndWaypoint(waypoint, null, 0.0);
    }
}
