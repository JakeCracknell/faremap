package com.cracknellj.fare.atoc;

import com.cracknellj.fare.dao.StationDAO;
import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
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
    private Map<String, FareSet> faresByStationId = new HashMap<>();

    public AtocDataReader() {
        try {
            readDataFromFiles();
            cleanupConflictingData();
            convertDataIntoFares();
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Failed to read ATOC data");
        }
    }

    private void readDataFromFiles() throws SQLException, IOException {
        crsToStation = new StationDAO().getStations().stream().filter(s -> s.crs != null).collect(toMap(s -> s.crs, s -> s));
        stationClusters = new StationClusterFileReader().getStationClusters();
        nlcToCRSMap = new LocationFileReader().getNLCToCRSMap();
        stationGroups = new LocationFileReader().getStationGroups();
        atocRoutes = new RouteFileReader().getAtocRoutes();
        rawFaresList = new FareFlowFileReader().getFaresList();
    }

    private void cleanupConflictingData() {
        LOG.info("Removing the following stations, as they are not in the database: " +
                nlcToCRSMap.values().stream().filter(crs -> !crsToStation.containsKey(crs)).collect(Collectors.toSet()));
        nlcToCRSMap.entrySet().removeIf(e -> !crsToStation.containsKey(e.getValue()));
    }

    private void convertDataIntoFares() {
        for (AtocFare fare : rawFaresList) {
            fare.fareDetail.appendToRouteDescription(atocRoutes.get(fare.routeCode).description);
            List<String> fromIds = getStationIDsFromNLC(fare.fromNlc);
            List<String> toIds = getStationIDsFromNLC(fare.toNlc);
            addFareForEach(fare.fareDetail, fromIds, toIds);
            if (fare.reversible) {
                addFareForEach(fare.fareDetail, toIds, fromIds);
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
        List<String> nlcs = stationClusters.getOrDefault(nlc, Lists.newArrayList(nlc));
        Stream<String> crssFromDirectMappings = nlcs.stream().filter(nlcToCRSMap::containsKey).map(nlcToCRSMap::get);
        Stream<String> crssFromStationGroups = nlcs.stream().filter(stationGroups::containsKey).map(stationGroups::get).flatMap(Collection::stream);
        List<String> crss = Stream.concat(crssFromDirectMappings, crssFromStationGroups).collect(Collectors.toList());
        return crss.stream().filter(crsToStation::containsKey).map(crsToStation::get).map(s -> s.stationId).collect(Collectors.toList());
    }

    public Map<String, FareSet> getFareSetsByStationId() {
        return faresByStationId;
    }
}
