package com.cracknellj.fare.atoc;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.objects.FareDetail;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class AtocDataReader {
    private static final Logger LOG = LogManager.getLogger(AtocDataReader.class);

    private Map<String, Station> crsToStation;
    private Map<String, List<String>> stationClusters;
    private Map<String, String> nlcToCRSMap;
    private Map<String, Set<String>> stationGroups;
    private Map<String, AtocRouteDetails> atocRoutes;
    private List<AtocFare> rawFaresList;
    private Map<String, List<String>> nlcToStationIDsMap = new HashMap<>();
    private Map<String, FareSet> faresByStationId = new HashMap<>();

    public AtocDataReader() {
        try {
            readDataFromFiles();
            cleanupConflictingData();
            convertDataIntoFares();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read ATOC data", e);
        }
    }

    private void readDataFromFiles() throws IOException {
        crsToStation = StationFileReader.getStations().stream().filter(s -> s.crs != null).collect(toMap(s -> s.crs, s -> s));
        stationClusters = new StationClusterFileReader().getStationClusters();
        nlcToCRSMap = new LocationFileReader().getNLCToCRSMap();
        stationGroups = new LocationFileReader().getStationGroups();
        atocRoutes = new RouteFileReader().getAtocRoutes();
        rawFaresList = new FareFlowFileReader().getFaresList();
    }

    private void cleanupConflictingData() {
        Set<String> unknownStations = nlcToCRSMap.values().stream().filter(crs -> !crsToStation.containsKey(crs)).collect(Collectors.toSet());
        LOG.info("Removing unknown stations, of which there are " + unknownStations.size());
        nlcToCRSMap.entrySet().removeIf(e -> !crsToStation.containsKey(e.getValue()));
    }

    private void convertDataIntoFares() {
        Set<String> keysToKeep = rawFaresList.stream()
                .filter(f -> !stationClusters.containsKey(f.fromNlc) && !stationClusters.containsKey(f.toNlc))
                .map(AtocFare::getKey).collect(Collectors.toSet());

        for (AtocFare fare : rawFaresList) {
            fare.fareDetail.routeDescription = atocRoutes.get(fare.routeCode).description;
            List<String> fromNlcs = stationClusters.getOrDefault(fare.fromNlc, Collections.singletonList(fare.fromNlc));
            List<String> toNlcs = stationClusters.getOrDefault(fare.toNlc, Collections.singletonList(fare.toNlc));
            boolean isOverridable = stationClusters.containsKey(fare.fromNlc) || stationClusters.containsKey(fare.toNlc);

            for (String fromClusterNlc : fromNlcs) {
                List<String> fromStationIds = getStationIDsFromNLC(fromClusterNlc);
                for (String toClusterNlc : toNlcs) {
                    String clusterKey = AtocFare.getKey(fromClusterNlc, toClusterNlc, fare.routeCode);
                    if (!isOverridable || !keysToKeep.contains(clusterKey)) {
                        List<String> toStationIds = getStationIDsFromNLC(toClusterNlc);
                        addFareForEach(fare.fareDetail, fromStationIds, toStationIds);
                        if (fare.reversible) { //might be incorrect. what if one direction is overridden?
                            addFareForEach(fare.fareDetail, toStationIds, fromStationIds);
                        }
                    }  // ELSE Cluster fare is to be overwritten by non-cluster fare. e.g. York to Selby.
                }
            }
        }
    }

    private void addFareForEach(FareDetail fareDetail, List<String> fromIds, List<String> toIds) {
        for (String fromId : fromIds) {
            for (String toId : toIds) {
                if (!fromId.equals(toId)) {
                    faresByStationId.computeIfAbsent(fromId, x -> new FareSet(fromId))
                            .add(toId, fareDetail);
                }
            }
        }
    }

    private List<String> getStationIDsFromNLC(String nlc) {
        return nlcToStationIDsMap.computeIfAbsent(nlc, x -> {
            List<String> nlcs = stationClusters.getOrDefault(nlc, Lists.newArrayList(nlc));
            Stream<String> crssFromDirectMappings = nlcs.stream().filter(nlcToCRSMap::containsKey).map(nlcToCRSMap::get);
            Stream<String> crssFromStationGroups = nlcs.stream().filter(stationGroups::containsKey).map(stationGroups::get).flatMap(Collection::stream);
            List<String> crss = Stream.concat(crssFromDirectMappings, crssFromStationGroups).collect(Collectors.toList());
            return crss.stream().filter(crsToStation::containsKey).map(crsToStation::get).map(s -> s.stationId).collect(Collectors.toList());
        });
    }

    public Map<String, FareSet> getFareSetsByStationId() {
        return faresByStationId;
    }
}
