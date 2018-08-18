package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.Station;

import java.util.*;
import java.util.stream.Collectors;

public class MultiHopFareDetailBuilder {
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
        while (node != null) {
            nodes.addFirst(node);
            node = predecessors.get(node);
        }
        return Collections.singletonList(getFareDetailFromNodes(nodes));
    }

    private FareDetail getFareDetailFromNodes(LinkedList<FareDetailAndWaypoint> nodes) {
        FareDetail fareDetail = new FareDetail(nodes);
        fareDetail.routeDescription = "Via " + nodes.stream().limit(nodes.size() - 1)
                .map(n -> stations.get(n.waypoint).stationName).collect(Collectors.joining(", "));
        return fareDetail;
    }
}
