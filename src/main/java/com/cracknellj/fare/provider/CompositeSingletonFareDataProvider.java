package com.cracknellj.fare.provider;

import com.cracknellj.fare.objects.FareSet;

import java.util.Map;
import java.util.stream.Stream;

public class CompositeSingletonFareDataProvider implements FareDataProvider {
    private final Map<String, FareSet> fareSets;

    private static FareDataProvider ourInstance = new CompositeSingletonFareDataProvider();

    private CompositeSingletonFareDataProvider() {
        fareSets = Stream.of(new AtocDataProvider(), new TFLDataProvider())
                .map(FareDataProvider::getAllFareSets).reduce(FareSet::combine).get();

       // combineFaresForStationsWithMatchingLocations();
    }

//    private void combineFaresForStationsWithMatchingLocations() {
//        try {
//            new StationDAO().getStations().stream().collect(Collectors.groupingBy(s -> s.latitude * s.longitude, Collectors.toSet()))
//                    .values().stream().filter(col -> col.size() > 1).forEach(this::combineFaresForSingleSetOfStations);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    private void combineFaresForSingleSetOfStations(Set<Station> stations) {
//        for (Station stationTakingFrom : stations) {
//            FareSet fareSetTakingFrom = fareSets.get(stationTakingFrom.stationId);
//            fareSetTakingFrom.fares.values().stream().flatMap(Collection::stream)
//                    .forEach(f -> f.appendToRouteDescription(stationTakingFrom.modes.toString()));
//            for (Station stationAddingTo : stations) {
//                FareSet fareSetAddingTo = fareSets.get(stationAddingTo.stationId);
//                fareSetTakingFrom.fares.forEach((toId, faresTakingFrom) -> {
//                    List<FareDetail> faresAddingTo = fareSetAddingTo.fares.get(toId);
//                    if (faresAddingTo != null) {
//                        faresTakingFrom.forEach(f -> f.appendToRouteDescription(stationTakingFrom.modes.toString()));
//                        faresAddingTo.forEach(f -> f.appendToRouteDescription(stationAddingTo.modes.toString()));
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
