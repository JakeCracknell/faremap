package com.cracknellj.fare.provider;

import com.cracknellj.fare.Haversine;
import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareDetailCollection;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalkingFareDataProvider implements FareDataProvider {
    private static final double DISTANCE_KM_THRESHOLD = 1.0;
    private static final double DISTANCE_KM_TO_MINUTES = 12; // or 5 km/h
    private final Map<String, FareSet> fareSetMap = new HashMap<>();

    public WalkingFareDataProvider() {
        List<Station> stations = StationFileReader.getStations();
        stations.forEach(fromStation -> {
            stations.forEach(toStation -> {
                double distance = Haversine.distance(fromStation, toStation);
                if (fromStation != toStation && distance < DISTANCE_KM_THRESHOLD) {
                    FareDetailCollection fareDetailCollection = new FareDetailCollection(1);
                    FareDetail fareDetail = new FareDetail(0, false, "Walk", true, false);
                    fareDetail.routeDescription = String.format("%.1f km, a %.0f minutes walk", distance, distance * DISTANCE_KM_TO_MINUTES);
                    fareDetailCollection.add(fareDetail);
                    fareSetMap.computeIfAbsent(fromStation.stationId, x -> new FareSet(fromStation.stationId))
                            .fares.put(toStation.stationId, fareDetailCollection);
                }
            });
        });
    }

    @Override
    public Map<String, FareSet> getAllFareSets() {
        return fareSetMap;
    }
}
