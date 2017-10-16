package com.cracknellj.fare.objects;

import java.math.BigDecimal;
import java.util.Objects;

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

    public boolean equalsExceptForPrice(FareDetail other) {
        return offPeakOnly == other.offPeakOnly &&
                isDefaultRoute == other.isDefaultRoute &&
                isTFL == other.isTFL &&
                Objects.equals(routeDescription, other.routeDescription) &&
                Objects.equals(accounting, other.accounting);
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
