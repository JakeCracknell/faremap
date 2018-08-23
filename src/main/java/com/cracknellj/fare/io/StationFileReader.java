package com.cracknellj.fare.io;

import com.cracknellj.fare.objects.Station;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StationFileReader {

    private static List<Station> stationList = Collections.emptyList();

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
        return stationList;
    }

}
