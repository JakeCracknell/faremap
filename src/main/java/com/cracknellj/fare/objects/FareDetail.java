package com.cracknellj.fare.objects;

import java.math.BigDecimal;

public class FareDetail {
    public final BigDecimal price;
    public final String mode;
    public final boolean offPeakOnly;
    public final String routeDescription;
    public final boolean isDefaultRoute;
    public final String accounting;

    public FareDetail(BigDecimal price, String mode, boolean offPeakOnly, String routeDescription, boolean isDefaultRoute, String accounting) {
        this.price = price;
        this.mode = mode;
        this.offPeakOnly = offPeakOnly;
        this.routeDescription = routeDescription;
        this.isDefaultRoute = isDefaultRoute;
        this.accounting = accounting;
    }

    @Override
    public String toString() {
        return "FareDetail{" +
                "price=" + price +
                ", mode='" + mode + '\'' +
                ", offPeakOnly=" + offPeakOnly +
                ", routeDescription='" + routeDescription + '\'' +
                ", isDefaultRoute=" + isDefaultRoute +
                ", accounting='" + accounting + '\'' +
                '}';
    }
}
