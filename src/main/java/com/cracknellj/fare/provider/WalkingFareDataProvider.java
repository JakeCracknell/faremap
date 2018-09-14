package com.cracknellj.fare.provider;

import com.cracknellj.fare.Haversine;
import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO - to replace with OSIs. This is good, but will give a journey like HAT -> CHX -> LECSQ -> PICC, totalling >0.5km walk
public class WalkingFareDataProvider implements FareDataProvider {

    private static final FareDetail WALKING_FARE_DETAIL = new FareDetail(0, false, "Walk", true, false);
    private final Map<String, FareSet> fareSetMap = new HashMap<>();

    public WalkingFareDataProvider() {
        List<Station> stations = StationFileReader.getStations();
        stations.forEach(fromStation -> {
            stations.forEach(toStation -> {
                if (Haversine.distance(fromStation, toStation) < 0.5) {
                    fareSetMap.computeIfAbsent(fromStation.stationId, x -> new FareSet(fromStation.stationId))
                            .fares.put(toStation.stationId, Collections.singletonList(WALKING_FARE_DETAIL));
                }
            });
        });
    }

    @Override
    public Map<String, FareSet> getAllFareSets() {
        return fareSetMap;
    }
}
