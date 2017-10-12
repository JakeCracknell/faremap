package com.cracknellj.fare.objects;

import java.math.BigDecimal;

public class FareDetail {
    public final BigDecimal price;
    public final boolean offPeakOnly;
    public String routeDescription;
    public final boolean isDefaultRoute;
    public final String accounting;
    public final boolean isTFL;

    public FareDetail(BigDecimal price, boolean offPeakOnly, String routeDescription, boolean isDefaultRoute, String accounting, boolean isTFL) {
        this.price = price;
        this.offPeakOnly = offPeakOnly;
        this.routeDescription = routeDescription;
        this.isDefaultRoute = isDefaultRoute;
        this.accounting = accounting;
        this.isTFL = isTFL;
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
                ", accounting='" + accounting + '\'' +
                ", isTFL=" + isTFL +
                '}';
    }
}
