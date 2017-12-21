package com.cracknellj.fare.provider;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.routefinding.DijkstraRouteFinder;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CompositeSingletonFareDataProvider implements FareDataProvider {
    private final Map<String, FareSet> fareSets;
    private final List<Station> stations;

    private static FareDataProvider ourInstance = new CompositeSingletonFareDataProvider();

    private CompositeSingletonFareDataProvider() {
        stations = StationFileReader.getStations();
        fareSets = Stream.of(new AtocDataProvider(), new TFLDataProvider())
                .map(FareDataProvider::getAllFareSets).reduce(FareSet::combine).get();

       // combineFaresForStationsWithMatchingLocations();
    }

    @Override
    public FareSet getFaresFrom(String fromId) {
        DijkstraRouteFinder dijkstraRouteFinder = new DijkstraRouteFinder(stations, this); //hmmmm
        FareSet fareSet = dijkstraRouteFinder.findCheapestRoutes(fromId);
        return FareSet.combine(fareSets.get(fromId), fareSet);
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
