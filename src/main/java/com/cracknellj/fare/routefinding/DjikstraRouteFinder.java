package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.FareDataProvider;
import jersey.repackaged.com.google.common.collect.Maps;
import jersey.repackaged.com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class DjikstraRouteFinder {
    public static final double MAX_PRICE = Double.MAX_VALUE;
    private final Map<String, Station> stations;
    private final FareDataProvider fareDataProvider;

    public DjikstraRouteFinder(Collection<Station> stations, FareDataProvider fareDataProvider) {
        this.stations = Maps.uniqueIndex(stations, s -> s.stationId);
        this.fareDataProvider = fareDataProvider;
    }

    public MultiTicketRoute findBestRoute(String fromId, String toId) {
        Set<String> unsettled = Sets.newHashSet(fromId);
        Set<String> settled = Sets.newHashSet();
        Map<String, Double> minFaresForStations = new HashMap<>(stations.size());
        stations.keySet().forEach(s -> minFaresForStations.put(s, MAX_PRICE));
        minFaresForStations.put(fromId, 0.0);
        Map<String, String> predecessors = new HashMap<>(stations.size());

        while (!unsettled.isEmpty()) {
            String node = unsettled.stream().sorted(Comparator.comparingDouble(minFaresForStations::get)).findFirst().get();
            unsettled.remove(node);
            settled.add(node);
            Double fareToNode = minFaresForStations.get(node);
            for (String nextStationId : stations.keySet()) {
                if (settled.contains(nextStationId)) continue;
                double proposedFare = fareToNode + getFare(node, nextStationId);
                if (minFaresForStations.get(nextStationId) > proposedFare) {
                    minFaresForStations.put(nextStationId, proposedFare);
                    predecessors.put(nextStationId, node);
                    unsettled.add(nextStationId);
                }
            }
        }

        LinkedList<String> path = new LinkedList<String>();
        LinkedList<Double> cumulativeCost = new LinkedList<Double>();

        String step = toId;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            cumulativeCost.add(minFaresForStations.get(step));
            step = predecessors.get(step);
            path.add(step);
        }
        cumulativeCost.add(0.0);
        // Put it into the correct order
        Collections.reverse(path);
        Collections.reverse(cumulativeCost);
        List<Station> stationList = path.stream().map(stations::get).collect(Collectors.toList());
        return new MultiTicketRoute(stationList, cumulativeCost);
    }

    private Double getFare(String fromId, String toId) {
        return fareDataProvider.getFares(fromId, toId).stream()
                .sorted(Comparator.comparing(f -> f.price)).findFirst()
                .map(fareDetail -> fareDetail.price.doubleValue()).orElse(MAX_PRICE);
    }


}
