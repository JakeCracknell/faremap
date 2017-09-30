package com.cracknellj.fare.atoc;

import com.cracknellj.fare.dao.StationDAO;
import com.cracknellj.fare.objects.Fare;
import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.Station;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class AtocDataReader {
    private static final Logger LOG = LogManager.getLogger(AtocDataReader.class);

    private Map<String, Station> crsToStation;
    private Map<String, List<String>> stationClusters;
    private Map<String, String> nlcToCRSMap;
    private Map<String, List<String>> stationGroups;
    private List<Fare> rawFaresList;
    private Map<String, Map<String, List<FareDetail>>> faresByStationId = new HashMap<>();

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
        rawFaresList = new FareFlowFileReader().getFaresList();
    }

    private void cleanupConflictingData() {
        LOG.info("Removing the following stations, as they are not in the database: " +
                nlcToCRSMap.values().stream().filter(crs -> !crsToStation.containsKey(crs)).collect(Collectors.toSet()));
        nlcToCRSMap.entrySet().removeIf(e -> !crsToStation.containsKey(e.getValue()));
    }

    private void convertDataIntoFares() {
        for (Fare fare : rawFaresList) {
            addFareForEach(fare.fareDetail, getStationIDsFromNLC(fare.fromId), getStationIDsFromNLC(fare.toId));
        }
    }

    private void addFareForEach(FareDetail fareDetail, List<String> fromIds, List<String> toIds) {
        for (String fromId : fromIds) {
            for (String toId : toIds) {
                faresByStationId.computeIfAbsent(fromId, x -> new HashMap<>())
                        .computeIfAbsent(toId, x -> new ArrayList<>())
                        .add(fareDetail);
            }
        }
    }

    private List<String> getStationIDsFromNLC(String nlc) {
        List<String> nlcs = stationClusters.getOrDefault(nlc, Lists.newArrayList(nlc));
        Stream<String> crssFromDirectMappings = nlcs.stream().filter(nlcToCRSMap::containsKey).map(nlcToCRSMap::get);
        Stream<String> crssFromStationGroups = nlcs.stream().filter(stationGroups::containsKey).map(stationGroups::get).flatMap(List::stream);
        List<String> crss = Stream.concat(crssFromDirectMappings, crssFromStationGroups).collect(Collectors.toList());
        return crss.stream().filter(crsToStation::containsKey).map(crsToStation::get).map(s -> s.stationId).collect(Collectors.toList());
    }

    public Map<String, Map<String, List<FareDetail>>> getFaresByStationId() {
        return faresByStationId;
    }
}