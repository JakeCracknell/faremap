package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;

import java.math.BigDecimal;
import java.util.List;

public class MultiHopFareDetail extends FareDetail {
    public final List<FareDetailAndWaypoint> hops;

    public MultiHopFareDetail(BigDecimal price, boolean offPeakOnly, String routeDescription, boolean isDefaultRoute,
                              String accounting, boolean isTFL, List<FareDetailAndWaypoint> hops) {
        super(price, offPeakOnly, routeDescription, isDefaultRoute, accounting, isTFL);
        this.hops = hops;
    }


}
