package com.cracknellj.fare.io;

import com.cracknellj.fare.Haversine;
import com.cracknellj.fare.objects.Station;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class StationFileReader {
    private static final double WALKING_DISTANCE_THRESHOLD_KM = 1;
    private static List<Station> stationList = Collections.emptyList();
    private static Map<String, Set<String>> nearbyStationsMap = new HashMap<>();

    public static List<Station> getStations() {
        if (stationList.isEmpty()) {
            try (Reader reader = Files.newBufferedReader(Paths.get("web/data/stations.json"))) {
                final Gson gson = new Gson();
                Station[] stations = gson.fromJson(new JsonReader(reader), Station[].class);
                stationList = Arrays.asList(stations);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        computeNearbyStationsMap();
        return stationList;
    }

    public static Map<String, Station> getStationsAsMap() {
        return Maps.uniqueIndex(getStations(), s -> s.stationId);
    }

    public static synchronized void computeNearbyStationsMap() {
        stationList.forEach(fromStation -> {
            Set<String> nearbyStationsForThisStation = new HashSet<>();
            stationList.forEach(toStation -> {
                double distance = Haversine.distance(fromStation, toStation);
                if (fromStation != toStation && distance < WALKING_DISTANCE_THRESHOLD_KM) {
                    nearbyStationsForThisStation.add(toStation.stationId);
                }
            });
            nearbyStationsMap.put(fromStation.stationId, nearbyStationsForThisStation);
        });
    }

    public static Set<String> getNearbyStations(String stationId) {
        if (nearbyStationsMap == null) {
            computeNearbyStationsMap();
        }
        return nearbyStationsMap.get(stationId);
    }
}
