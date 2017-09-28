package com.cracknellj.fare.atoc;

import com.cracknellj.fare.dao.FareDAO;
import com.cracknellj.fare.dao.StationDAO;
import com.cracknellj.fare.objects.Fare;
import com.cracknellj.fare.objects.Station;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AtocDataImporter {
    private static final Logger LOG = LogManager.getLogger(AtocDataImporter.class);

    public static void main(String[] args) throws Exception {
        Map<String, Station> crsToStation = new StationDAO().getStations().stream().filter(s -> s.crs != null).collect(Collectors.toMap(s -> s.crs, s -> s));
        Map<String, List<String>> stationClusters = new StationClusterFileReader().getStationClusters();
        Map<String, String> nlcToCRSMap = new LocationFileReader().getNLCToCRSMap();
        Map<String, List<String>> stationGroups = new LocationFileReader().getStationGroups();
        List<Fare> rawFaresList = new FareFlowFileReader().getFaresList();
        List<Fare> faresToInsert = new ArrayList<>();

        LOG.info("Removing the following stations, as they are not in the database: " +
                nlcToCRSMap.values().stream().filter(crs -> !crsToStation.containsKey(crs)).collect(Collectors.toSet()));
        nlcToCRSMap.entrySet().removeIf(e -> !crsToStation.containsKey(e.getValue()));

        for (Fare fare : rawFaresList) {
            try {
                List<String> fromNLCs = stationClusters.getOrDefault(fare.fromId, Lists.newArrayList(fare.fromId));
                List<String> fromCRSs = fromNLCs.stream().filter(nlcToCRSMap::containsKey).map(nlcToCRSMap::get).collect(Collectors.toList());
                fromCRSs.addAll(stationGroups.getOrDefault(fare.fromId, Lists.newArrayList()));
                List<String> fromIds = fromCRSs.stream().filter(crsToStation::containsKey).map(crsToStation::get).map(s -> s.stationId).collect(Collectors.toList());

                List<String> toNLCs = stationClusters.getOrDefault(fare.toId, Lists.newArrayList(fare.toId));
                List<String> toCRSs = toNLCs.stream().filter(nlcToCRSMap::containsKey).map(nlcToCRSMap::get).collect(Collectors.toList());
                toCRSs.addAll(stationGroups.getOrDefault(fare.toId, Lists.newArrayList()));
                List<String> toIds = toCRSs.stream().filter(crsToStation::containsKey).map(crsToStation::get).map(s -> s.stationId).collect(Collectors.toList());

                for (String fromId : fromIds) {
                    for (String toId : toIds) {
                        faresToInsert.add(new Fare(fromId, toId, fare.fareDetail));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        faresToInsert.removeIf(f -> !f.fromId.equals("910GHATFILD"));
        new FareDAO().bulkInsertFares(faresToInsert);
    }
}

