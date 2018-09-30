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
    public final String ticketName;
    public final boolean isDefaultRoute;
    public boolean isTFL;
    public String routeDescription = "";

    public FareDetail(int price, boolean offPeakOnly, String ticketName, boolean isDefaultRoute, boolean isTFL) {
        this.price = price;
        this.offPeakOnly = offPeakOnly;
        this.ticketName = ticketName;
        this.isDefaultRoute = isDefaultRoute;
        this.isTFL = isTFL;
        this.hops = null;
    }

    public FareDetail(List<FareDetailAndWaypoint> hops, boolean offPeakOnly) {
        this.ticketName = "Split Ticket";
        this.hops = hops;
        this.isDefaultRoute = false;
        this.offPeakOnly = offPeakOnly;
        this.isTFL = hops.stream().allMatch(h -> h.fareDetail.isTFL);
        this.price = hops.stream().mapToInt(h -> h.fareDetail.price).sum();
    }

    public boolean equalsExceptForPrice(FareDetail other) {
        return offPeakOnly == other.offPeakOnly &&
                isDefaultRoute == other.isDefaultRoute &&
                isTFL == other.isTFL &&
                Objects.equals(ticketName, other.ticketName) &&
                Objects.equals(routeDescription, other.routeDescription);
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
