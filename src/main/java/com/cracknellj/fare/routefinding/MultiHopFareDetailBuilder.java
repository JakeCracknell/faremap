package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.Station;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class MultiHopFareDetailBuilder {
    private static final boolean OFF_PEAK = true;
    private final Map<String, Station> stations;
    private Map<FareDetailAndWaypoint, FareDetailAndWaypoint> predecessors;

    public MultiHopFareDetailBuilder(Map<String, Station> stations, Map<FareDetailAndWaypoint, FareDetailAndWaypoint> predecessors) {
        this.stations = stations;
        this.predecessors = predecessors;
        cleanPredecessors();
    }

    public Map<String, List<FareDetail>> createMap() {
        return predecessors.keySet().stream().collect(Collectors.toMap(n -> n.waypoint, this::getFareDetailsForDestinationNode));
    }

    private void cleanPredecessors() {
        Map<FareDetailAndWaypoint, FareDetailAndWaypoint> cleanedPredecessors = new HashMap<>(predecessors.size());
        predecessors.forEach((to, from) -> {
            if (from.fareDetail != null) {
                cleanedPredecessors.put(to, from);
            }
        });
        predecessors = cleanedPredecessors;
    }

    private List<FareDetail> getFareDetailsForDestinationNode(FareDetailAndWaypoint node) {
        LinkedList<FareDetailAndWaypoint> nodes = new LinkedList<>();
        BigDecimal accuratePrice = BigDecimal.ZERO;
        while (node != null) {
            accuratePrice = accuratePrice.add(node.fareDetail.price);
            nodes.addFirst(node);
            node = predecessors.get(node);
        }
        String routeDescription = "Split ticket via " + nodes.stream().skip(1)
                .map(n -> stations.get(n.waypoint).stationName).collect(Collectors.joining(", "));
        FareDetail fareDetail = new MultiHopFareDetail(accuratePrice, OFF_PEAK, routeDescription, false, "ST", false, nodes);
        return Collections.singletonList(fareDetail);
    }
}
