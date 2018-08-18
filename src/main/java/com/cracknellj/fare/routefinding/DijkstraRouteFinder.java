package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.Haversine;
import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.FareDataProvider;
import jersey.repackaged.com.google.common.collect.Maps;
import jersey.repackaged.com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

public class DijkstraRouteFinder {
    public static final int MAX_PRICE = Integer.MAX_VALUE;

    private final Map<String, Station> stations;
    private final FareDataProvider fareDataProvider;

    public DijkstraRouteFinder(Collection<Station> stations, FareDataProvider fareDataProvider) {
        this.stations = Maps.uniqueIndex(stations, s -> s.stationId);
        this.fareDataProvider = fareDataProvider;
    }

    public FareSet findCheapestRoutes(String fromId) {
        Set<String> unsettled = Sets.newHashSet(fromId);
        Set<String> settled = Sets.newHashSet();
        Map<String, Integer> minFaresForStations = new HashMap<>(stations.size());
        stations.keySet().forEach(s -> minFaresForStations.put(s, MAX_PRICE));
        minFaresForStations.put(fromId, 0);
        Map<String, String> predecessors = new HashMap<>(stations.size());
        Map<String, FareDetailAndWaypoint> stationIdToNode = new HashMap<>(stations.size());
        stationIdToNode.put(fromId, FareDetailAndWaypoint.startNode(fromId));

        Comparator<String> nextNodeChooserComparator = Comparator.comparingInt(minFaresForStations::get);
        while (!unsettled.isEmpty()) {
            String node = unsettled.stream().sorted(nextNodeChooserComparator).findFirst().get();
            unsettled.remove(node);
            settled.add(node);
            Integer fareToNode = minFaresForStations.get(node);
            for (String nextStationId : stations.keySet()) {
                if (!settled.contains(nextStationId)) {
                    getFareDetailIfExists(node, nextStationId).ifPresent(fareDetail -> {
                        int proposedFare = fareToNode + fareDetail.price;
                        Integer existingFare = minFaresForStations.get(nextStationId);
                        if (existingFare > proposedFare) {
                            minFaresForStations.put(nextStationId, proposedFare);
                            FareDetailAndWaypoint nextNode = new FareDetailAndWaypoint(nextStationId, fareDetail);
                            predecessors.put(nextStationId, node);
                            unsettled.add(nextStationId);
                            stationIdToNode.put(nextStationId, nextNode);
                        } else if (existingFare == proposedFare &&
                                isProposedViaPointCloser(nextStationId, predecessors.get(nextStationId), node)) {
                            FareDetailAndWaypoint nextNode = new FareDetailAndWaypoint(nextStationId, fareDetail);
                            predecessors.put(nextStationId, node);
                            unsettled.add(nextStationId);
                            stationIdToNode.put(nextStationId, nextNode);
                        }
                    });
                }
            }
        }
        Map<FareDetailAndWaypoint, FareDetailAndWaypoint> nodePredecessors = predecessors.entrySet().stream()
                .collect(Collectors.toMap(e -> stationIdToNode.get(e.getKey()), e -> stationIdToNode.get(e.getValue())));
        MultiHopFareDetailBuilder multiHopFareDetailBuilder = new MultiHopFareDetailBuilder(stations, nodePredecessors);
        return new FareSet(fromId, multiHopFareDetailBuilder.createMap());
    }

    private boolean isProposedViaPointCloser(String endStationId, String existingViaPointId, String proposedViaPointId) {
        Station nextStation = stations.get(endStationId);
        return Haversine.distance(nextStation, stations.get(proposedViaPointId)) <
                Haversine.distance(nextStation, stations.get(existingViaPointId));
    }


    private Optional<FareDetail> getFareDetailIfExists(String fromId, String toId) {
        return fareDataProvider.getFares(fromId, toId).stream()
                .filter(f -> !f.offPeakOnly)
                .sorted(Comparator.comparing(f -> f.price)).findFirst();
    }


}
