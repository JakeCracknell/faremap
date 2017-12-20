package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;

public class FareDetailAndWaypoint {
    public String waypoint;
    public FareDetail fareDetail;

    public FareDetailAndWaypoint(String waypoint, FareDetail fareDetail) {
        this.waypoint = waypoint;
        this.fareDetail = fareDetail;
    }

    public static FareDetailAndWaypoint startNode(String waypoint) {
        return new FareDetailAndWaypoint(waypoint, null);
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
