package com.cracknellj.fare.io;

import com.cracknellj.fare.objects.Station;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class StationFileWriter {

    public static void writeStations(List<Station> stations) {
        try (Writer writer = Files.newBufferedWriter(Paths.get("web/data/stations.json"))) {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(stations, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
