package com.cracknellj.fare.cleanup;

import com.cracknellj.fare.dao.FareDAO;
import com.cracknellj.fare.dao.StationDAO;
import com.cracknellj.fare.objects.Fare;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.tfl.TFLFareScraper;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FillTFLFareGaps {
    private static StationDAO stationDAO = new StationDAO();
    private static FareDAO fareDAO = new FareDAO();
    private static TFLFareScraper tflFareScraper = new TFLFareScraper();
    private static Set<String> stationIds;

    public static void main(String[] args) throws SQLException {
        stationIds = stationDAO.getStations().stream()
                .filter(s -> s.oysterAccepted)
                .map(s -> s.stationId).collect(Collectors.toSet());
        List<Fare> faresToQuery = stationIds.stream().flatMap(FillTFLFareGaps::findMissingFares)
                .collect(Collectors.toList());
        List<Fare> faresToInsert = faresToQuery.parallelStream()
                .map(tflFareScraper::lookupFare)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        fareDAO.bulkInsertFares(faresToInsert);
    }

    private static Stream<Fare> findMissingFares(String fromStation) {
        try {
            FareSet fareSet = fareDAO.getFaresFrom(fromStation);
            Set<String> toStations = new HashSet<>(stationIds);
            toStations.remove(fromStation);
            toStations.removeAll(fareSet.fares.keySet());
            return toStations.stream().map(toStation -> new Fare(fromStation, toStation, null));
        } catch (SQLException e) {
            return Stream.empty();
        }
    }
}