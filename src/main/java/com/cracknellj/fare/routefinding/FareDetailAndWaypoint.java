package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;

import java.math.BigDecimal;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FareDetailAndWaypoint that = (FareDetailAndWaypoint) o;
        return waypoint.equals(that.waypoint);
    }

    @Override
    public int hashCode() {
        return waypoint.hashCode();
    }
}
