package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.Station;

import java.util.LinkedList;
import java.util.List;

public class MultiTicketRoute {
    private final List<Station> stationList;
    private final LinkedList<Double> cumulativeCost;

    public MultiTicketRoute(List<Station> stationList, LinkedList<Double> cumulativeCost) {
        this.stationList = stationList;
        this.cumulativeCost = cumulativeCost;
    }
}
