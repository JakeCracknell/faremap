package com.cracknellj.fare.objects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FareSetBuilder {
    private final String fromId;
    private final Map<String, List<FareDetail>> faresByToId;

    public FareSetBuilder(String fromId) {
        this.fromId = fromId;
        faresByToId = new HashMap<>();
    }

    public void addFare(String toId, BigDecimal price, boolean offPeakOnly, String routeDescription,
                        boolean isDefaultRoute, String accounting, boolean isTFL) {
        FareDetail fareDetail = new FareDetail(price, offPeakOnly, routeDescription, isDefaultRoute, accounting, isTFL);
        faresByToId.computeIfAbsent(toId, t -> new ArrayList<>()).add(fareDetail);
    }


    public FareSet create() {
        return new FareSet(fromId, faresByToId);
    }
}
