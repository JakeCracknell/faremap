package com.cracknellj.fare.lines;

import com.cracknellj.fare.Haversine;
import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.objects.TransportLine;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

//TODO this fails a lot because of lack of OSIs
public class TransportLinesGraph {
    private final Map<String, Station> stations;
    private final MutableNetwork<String, Edge> network;
    private final Map<String, Map<String, Double>> distances;

    public TransportLinesGraph() {
        this.network = NetworkBuilder.directed().build();
        stations = StationFileReader.getStationsAsMap();
        stations.values().forEach(s -> network.addNode(s.stationId));
        Arrays.stream(loadLinesFromFile()).flatMap(tl -> tl.branches.stream()).map(b -> b.stationIds).forEach(branch -> {
            for (int i = 0; i < branch.size() - 1; i++) {
                String from = branch.get(i);
                String to = branch.get(i + 1);
                Station station1 = stations.get(from);
                Station station2 = stations.get(to);
                if (station1 != null && station2 != null && !network.hasEdgeConnecting(from, to)) {
                    network.addEdge(from, to, new Edge(Haversine.distance(station1, station2)));
                }
            }
        });
        this.distances = new HashMap<>(stations.size());
    }

    private static TransportLine[] loadLinesFromFile() {
        try (Reader reader = Files.newBufferedReader(Paths.get("web", "data", "transport_lines.json"))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.fromJson(reader, TransportLine[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new TransportLine[0];
    }

    private Map<String, Double> getDistancesFromNode(String fromId) {
        Set<String> unsettled = Sets.newHashSet(fromId);
        Set<String> settled = Sets.newHashSet();
        Map<String, Double> bestDistances = stations.keySet().stream().collect(Collectors.toMap(s -> s, s -> Double.MAX_VALUE));
        bestDistances.put(fromId, 0d);

        while (!unsettled.isEmpty()) {
            String node = unsettled.stream().findFirst().get();
            unsettled.remove(node);
            settled.add(node);
            Double distanceToNode = bestDistances.get(node);
            for (String nextStationId : stations.keySet()) {
                if (!settled.contains(nextStationId)) {
                    network.edgeConnecting(node, nextStationId).ifPresent(edge -> {
                        double proposedFare = distanceToNode + edge.distance;
                        double existingFare = bestDistances.get(nextStationId);
                        if (existingFare > proposedFare) {
                            bestDistances.put(nextStationId, proposedFare);
                            unsettled.add(nextStationId);
                        }
                    });
                }
            }
        }
        return bestDistances;
    }

    public double getDistance(String fromId, String toId) {
        return distances.computeIfAbsent(fromId, x -> getDistancesFromNode(fromId)).get(toId);
    }

    private static class Edge {
        public double distance;

        private Edge(double distance) {
            this.distance = distance;
        }
    }
//
//    void updateDistancesFrom(String node) {
//        Map<String, Double> distancesFromNode = stations.keySet().stream().collect(Collectors.toMap(s -> s, s -> Double.MAX_VALUE));
//
//        for (String successor : network.successors(node)) {
//            distancesFromNode.put(successor, Haversine.distance(stations.get(node), stations.get(successor)));
//        }
//
//
//        for (E outEdge : network.outEdges(node)) {
//            N target = network.target(outEdge);
//            double targetDistance = nodeDistance + edgeWeights.apply(outEdge);
//            if (targetDistance < this.distances.getOrDefault(target, Double.MAX_VALUE)) {
//                this.distances.put(target, targetDistance);
//            }
//        }
//
//        this.distances.put(node, distancesFromNode);
//    }
}
