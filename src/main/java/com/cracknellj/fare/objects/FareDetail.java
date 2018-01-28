package com.cracknellj.fare.objects;

import com.cracknellj.fare.routefinding.FareDetailAndWaypoint;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

//For single-hop journeys, hops = null.
public class FareDetail {
    public final List<FareDetailAndWaypoint> hops;

    public final BigDecimal price;
    public final boolean offPeakOnly;
    public String routeDescription;
    public final boolean isDefaultRoute;
    public final boolean isTFL;

    public FareDetail(BigDecimal price, boolean offPeakOnly, String routeDescription, boolean isDefaultRoute, boolean isTFL) {
        this.price = price;
        this.offPeakOnly = offPeakOnly;
        this.routeDescription = routeDescription;
        this.isDefaultRoute = isDefaultRoute;
        this.isTFL = isTFL;
        this.hops = null;
    }

    public FareDetail(String routeDescription, List<FareDetailAndWaypoint> hops) {
        this.routeDescription = routeDescription;
        this.hops = hops;
        this.isDefaultRoute = false;
        this.offPeakOnly = hops.stream().anyMatch(h -> h.fareDetail.offPeakOnly) && false; //TODO make UI nicer
        this.isTFL = hops.stream().allMatch(h -> h.fareDetail.isTFL);
        this.price = hops.stream().map(h -> h.fareDetail.price).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean equalsExceptForPrice(FareDetail other) {
        return offPeakOnly == other.offPeakOnly &&
                isDefaultRoute == other.isDefaultRoute &&
                isTFL == other.isTFL &&
                Objects.equals(routeDescription, other.routeDescription);
    }

    public void appendToRouteDescription(String extraDescription) {
        routeDescription = routeDescription + ", " + extraDescription;
    }

    @Override
    public String toString() {
        return "FareDetail{" +
                "price=" + price +
                ", offPeakOnly=" + offPeakOnly +
                ", routeDescription='" + routeDescription + '\'' +
                ", isDefaultRoute=" + isDefaultRoute +
                ", isTFL=" + isTFL +
                '}';
    }
}
