package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.FareDataProvider;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

public abstract class DijkstraSplitTicketTask {
    public static final int MAX_PRICE = Integer.MAX_VALUE;

    private final Map<String, Station> stations;
    final FareDataProvider fareDataProvider;
    private final String fromId;

    private Set<String> unsettled;
    private Set<String> settled;
    private Map<String, Integer> minCostsForStations;
    private Map<String, String> predecessors;
    private Map<String, FareDetailAndWaypoint> stationIdToNode;
    private Comparator<String> nextNodeChooserComparator;


    public DijkstraSplitTicketTask(Map<String, Station> stations, FareDataProvider fareDataProvider, String fromId) {
        this.stations = stations;
        this.fareDataProvider = fareDataProvider;
        this.fromId = fromId;
    }

    public FareSet findCheapestRoutes() {
        initialise();
        settleAllNodes();
        return generateFareSet();
    }

    private void initialise() {
        unsettled = Sets.newHashSet(fromId);
        settled = Sets.newHashSet();
        minCostsForStations = new HashMap<>(stations.size());
        stations.keySet().forEach(s -> minCostsForStations.put(s, MAX_PRICE));
        minCostsForStations.put(fromId, 0);
        predecessors = new HashMap<>(stations.size());
        stationIdToNode = new HashMap<>(stations.size());
        stationIdToNode.put(fromId, FareDetailAndWaypoint.startNode(fromId));
        nextNodeChooserComparator = Comparator.comparingInt(minCostsForStations::get);
    }

    private void settleAllNodes() {
        while (!unsettled.isEmpty()) {
            String node = unsettled.stream().min(nextNodeChooserComparator).get();
            settleNode(node);
            expandSearchFromNode(node);
        }
    }

    // We have found the optimal route to this node.
    private void settleNode(String node) {
        unsettled.remove(node);
        settled.add(node);
    }

    private void expandSearchFromNode(String node) {
        Integer costToNode = minCostsForStations.get(node);
        for (String nextStationId : stations.keySet()) {
            if (!settled.contains(nextStationId)) {
                FareDetail fareDetail = getFareDetailIfExistsOrNull(node, nextStationId);
                if (fareDetail != null) {
                    int proposedCost = costToNode + fareDetail.price + 1; //1p penalty for every hop.
                    Integer existingCost = minCostsForStations.get(nextStationId);
                    if (existingCost > proposedCost) {
                        minCostsForStations.put(nextStationId, proposedCost);
                        FareDetailAndWaypoint nextNode = new FareDetailAndWaypoint(nextStationId, fareDetail);
                        predecessors.put(nextStationId, node);
                        stationIdToNode.put(nextStationId, nextNode);
                        unsettled.add(nextStationId);
                    }
                };
            }
        }
    }

    private FareSet generateFareSet() {
        Map<FareDetailAndWaypoint, FareDetailAndWaypoint> nodePredecessors = predecessors.entrySet().stream()
                .collect(Collectors.toMap(e -> stationIdToNode.get(e.getKey()), e -> stationIdToNode.get(e.getValue())));
        MultiHopFareDetailBuilder multiHopFareDetailBuilder = new MultiHopFareDetailBuilder(stations, nodePredecessors);
        return new FareSet(fromId, multiHopFareDetailBuilder.createMap());
    }


    abstract FareDetail getFareDetailIfExistsOrNull(String fromId, String toId);

}
