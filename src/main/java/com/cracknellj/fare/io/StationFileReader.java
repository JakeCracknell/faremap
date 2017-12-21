package com.cracknellj.fare.io;

import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StationFileReader {

    public static List<Station> getStations() {
        final File dataFile = new File("stations.json");
        try (final InputStreamReader inputStreamReader = new InputStreamReader(
                new BufferedInputStream(new FileInputStream(dataFile)))) {
            final Gson gson = new Gson();
            Station[] stations = gson.fromJson(new JsonReader(inputStreamReader), Station[].class);
            return Arrays.asList(stations);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
