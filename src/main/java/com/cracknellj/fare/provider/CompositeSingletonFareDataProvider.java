package com.cracknellj.fare.provider;

import com.cracknellj.fare.dao.StationDAO;
import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompositeSingletonFareDataProvider implements FareDataProvider {
    private final Map<String, FareSet> fareSets;

    private static FareDataProvider ourInstance = new CompositeSingletonFareDataProvider();

    public CompositeSingletonFareDataProvider() {
        fareSets = Stream.of(new AtocDataProvider(), new TFLDataProvider())
                .map(FareDataProvider::getAllFareSets).reduce(FareSet::combine).get();

        //combineFaresForStationsWithMatchingLocations();
    }
//
//    private void combineFaresForStationsWithMatchingLocations() {
//        try {
//            new StationDAO().getStations().stream().collect(Collectors.groupingBy(s -> s.latitude * s.longitude, Collectors.toSet()))
//                    .values().stream().filter(col -> col.size() > 1).forEach(this::combineFaresForSingleSetOfStations);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void combineFaresForSingleSetOfStations(Set<Station> stations) {
//        for (Station stationTakingFrom : stations) {
//            for (Station stationAddingTo : stations) {
//                FareSet fareSetTakingFrom = fareSets.get(stationTakingFrom.stationId);
//                FareSet fareSetAddingTo = fareSets.get(stationAddingTo.stationId);
//                fareSetTakingFrom.fares.forEach((toId, faresTakingFrom) -> {
//                    List<FareDetail> faresAddingTo = fareSetAddingTo.fares.get(toId);
//                    if (faresAddingTo != null) {
//                        fareSetTakingFrom.fares.forEach();
//                    }
//                });
//                if (stationTakingFrom != stationAddingTo) {
//
//                }
//            }
//        }
//
//    }

    public synchronized static FareDataProvider getInstance() {
        return ourInstance;
    }

    @Override
    public Map<String, FareSet> getAllFareSets() {
        return fareSets;
    }
}
