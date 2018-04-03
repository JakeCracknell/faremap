package com.cracknellj.fare.objects;

import com.cracknellj.fare.routefinding.FareDetailAndWaypoint;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

//For single-hop journeys, hops = null.
public class FareDetail {
    public final List<FareDetailAndWaypoint> hops;

    public final int price; //in GBP pence
    public final boolean offPeakOnly;
    public String routeDescription;
    public final boolean isDefaultRoute;
    public final boolean isTFL;

    public FareDetail(int price, boolean offPeakOnly, String routeDescription, boolean isDefaultRoute, boolean isTFL) {
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
        this.price = hops.stream().mapToInt(h -> h.fareDetail.price).sum();
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
                "price=" + NumberFormat.getCurrencyInstance(Locale.UK).format(price / 100.0) +
                ", offPeakOnly=" + offPeakOnly +
                ", routeDescription='" + routeDescription + '\'' +
                ", isDefaultRoute=" + isDefaultRoute +
                ", isTFL=" + isTFL +
                '}';
    }
}
