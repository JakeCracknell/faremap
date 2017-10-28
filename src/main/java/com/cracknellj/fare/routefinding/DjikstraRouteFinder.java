package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.FareDataProvider;
import jersey.repackaged.com.google.common.collect.Maps;
import jersey.repackaged.com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class DjikstraRouteFinder {
    public static final double MAX_PRICE = Double.MAX_VALUE;
    private static final boolean OFF_PEAK = true;
    //public static final FareDetail NO_FARE = new FareDetail(BigDecimal.valueOf(MAX_PRICE), false, "NO", false, "", false);

    private final Map<String, Station> stations;
    private final FareDataProvider fareDataProvider;

    public DjikstraRouteFinder(Collection<Station> stations, FareDataProvider fareDataProvider) {
        this.stations = Maps.uniqueIndex(stations, s -> s.stationId);
        this.fareDataProvider = fareDataProvider;
    }

    public FareSet findCheapestRoutes(String fromId) {
        FareDetailAndWaypoint startNode = FareDetailAndWaypoint.startNode(fromId);
        Set<FareDetailAndWaypoint> unsettled = Sets.newHashSet(startNode);
        Set<String> settled = Sets.newHashSet();

        Map<String, Double> minFaresForStations = new HashMap<>(stations.size());
        stations.keySet().forEach(s -> minFaresForStations.put(s, MAX_PRICE));
        minFaresForStations.put(fromId, 0.0);

        Map<FareDetailAndWaypoint, FareDetailAndWaypoint> predecessors = new HashMap<>(stations.size());

        while (!unsettled.isEmpty()) {
            FareDetailAndWaypoint node = unsettled.stream().sorted(Comparator.comparingDouble(n -> n.cumulativeCost)).findFirst().get();
            unsettled.remove(node);
            settled.add(node.waypoint);
            for (String nextStationId : stations.keySet()) {
                if (!settled.contains(nextStationId)) {
                    getFareDetailIfExists(node.waypoint, nextStationId).ifPresent(fareDetail -> {
                        double proposedFare = node.cumulativeCost + fareDetail.price.doubleValue();
                        if (minFaresForStations.get(nextStationId) > proposedFare) {
                            minFaresForStations.put(nextStationId, proposedFare);
                            FareDetailAndWaypoint nextNode = new FareDetailAndWaypoint(nextStationId, fareDetail, proposedFare);
                            predecessors.put(nextNode, node);
                            unsettled.add(nextNode);
                        }
                    });
                }
            }
        }
        MultiHopFareDetailBuilder multiHopFareDetailBuilder = new MultiHopFareDetailBuilder(stations, predecessors);
        return new FareSet(fromId, multiHopFareDetailBuilder.createMap());
    }


    private Optional<FareDetail> getFareDetailIfExists(String fromId, String toId) {
        return fareDataProvider.getFares(fromId, toId).stream()
                .sorted(Comparator.comparing(f -> f.price)).findFirst();
    }


}
