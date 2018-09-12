package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.Station;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<FareDetailAndWaypoint> normalisedHops = nodes.stream()
                .flatMap(h -> h.fareDetail.hops != null ? h.fareDetail.hops.stream() : Stream.of(h)).collect(Collectors.toList());
        return Collections.singletonList(getFareDetailFromNodes(normalisedHops));
    }

    private FareDetail getFareDetailFromNodes(List<FareDetailAndWaypoint> nodes) {
        FareDetail fareDetail = new FareDetail(nodes);
        fareDetail.routeDescription = "Via " + nodes.stream().limit(nodes.size() - 1)
                .map(n -> stations.get(n.waypoint).stationName).collect(Collectors.joining(", "));
        return fareDetail;
    }
}
