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
    public final String ticketName;
    public String routeDescription = "";
    public final boolean isDefaultRoute;
    public final boolean offPeakOnly;
    public final boolean isTFL;
    public final boolean railcardsValid;

    public FareDetail(List<FareDetailAndWaypoint> hops, int price, String ticketName, String routeDescription, boolean isDefaultRoute, boolean offPeakOnly, boolean isTFL, boolean railcardsValid) {
        this.hops = hops;
        this.price = price;
        this.ticketName = ticketName;
        this.routeDescription = routeDescription;
        this.isDefaultRoute = isDefaultRoute;
        this.offPeakOnly = offPeakOnly;
        this.isTFL = isTFL;
        this.railcardsValid = railcardsValid;
    }

    public boolean equalsExceptForPrice(FareDetail other) {
        return offPeakOnly == other.offPeakOnly &&
                isDefaultRoute == other.isDefaultRoute &&
                isTFL == other.isTFL &&
                railcardsValid == other.railcardsValid &&
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
