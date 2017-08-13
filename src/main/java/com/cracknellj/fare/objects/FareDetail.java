package com.cracknellj.fare.objects;

import java.math.BigDecimal;

public class FareDetail {
    public final BigDecimal price;
    public final boolean offPeakOnly;
    public final String routeDescription;
    public final boolean isDefaultRoute;
    public final String accounting;

    public FareDetail(BigDecimal price, boolean offPeakOnly, String routeDescription, boolean isDefaultRoute, String accounting) {
        this.price = price;
        this.offPeakOnly = offPeakOnly;
        this.routeDescription = routeDescription;
        this.isDefaultRoute = isDefaultRoute;
        this.accounting = accounting;
    }

    @Override
    public String toString() {
        return "FareDetail{" +
                "price=" + price +
                ", offPeakOnly=" + offPeakOnly +
                ", routeDescription='" + routeDescription + '\'' +
                ", isDefaultRoute=" + isDefaultRoute +
                ", accounting='" + accounting + '\'' +
                '}';
    }
}
