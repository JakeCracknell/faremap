package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareDetailCollection;
import com.cracknellj.fare.objects.Station;

import java.util.*;
import java.util.stream.Collectors;

public class MultiHopFareDetailBuilder {
    private final Map<String, Station> stations;
    private Map<FareDetailAndWaypoint, FareDetailAndWaypoint> predecessors;
    private final boolean offPeakOnly;

    public MultiHopFareDetailBuilder(Map<String, Station> stations, Map<FareDetailAndWaypoint, FareDetailAndWaypoint> predecessors, boolean offPeakOnly) {
        this.stations = stations;
        this.predecessors = predecessors;
        this.offPeakOnly = offPeakOnly;
        cleanPredecessors();
    }

    public Map<String, FareDetailCollection> createMap() {
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

    private FareDetailCollection getFareDetailsForDestinationNode(FareDetailAndWaypoint node) {
        LinkedList<FareDetailAndWaypoint> nodes = new LinkedList<>();
        while (node != null) {
            nodes.addFirst(node);
            node = predecessors.get(node);
        }
        FareDetailCollection fareDetails = new FareDetailCollection(1);
        fareDetails.add(getFareDetailFromNodes(nodes));
        return fareDetails;
    }

    private FareDetail getFareDetailFromNodes(List<FareDetailAndWaypoint> nodes) {
        FareDetail fareDetail = new FareDetail(nodes, offPeakOnly);
        fareDetail.routeDescription = "Via " + nodes.stream().limit(nodes.size() - 1)
                .map(n -> stations.get(n.waypoint).stationName).collect(Collectors.joining(", "));
        return fareDetail;
    }
}
