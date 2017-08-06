package com.cracknellj.fare.ws.objects;

import java.math.BigDecimal;

public class FareDetail {
    private final BigDecimal price;
    private final boolean offPeakOnly;
    private final String routeDescription;
    private final boolean isDefaultRoute;
    private final String accounting;

    public FareDetail(BigDecimal price, boolean offPeakOnly, String routeDescription, boolean isDefaultRoute, String accounting) {
        this.price = price;
        this.offPeakOnly = offPeakOnly;
        this.routeDescription = routeDescription;
        this.isDefaultRoute = isDefaultRoute;
        this.accounting = accounting;
    }
}
